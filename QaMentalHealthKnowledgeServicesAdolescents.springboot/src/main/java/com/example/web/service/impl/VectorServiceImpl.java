package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.entity.*;
import com.example.web.enums.VectorStatusTypeEnum;
import com.example.web.mapper.*;
import com.example.web.service.VectorService;
import com.example.web.utils.LocalDocumentFiles;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class VectorServiceImpl implements VectorService {
    private final KnowledgeDocumentMapper knowledgeDocumentMapper;
    private final DocumentChunkMapper documentChunkMapper;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final TransactionTemplate transactions;
    @Value("${server.ip:http://localhost:7406}")
    private String publicBase = "http://localhost:7406";
    private final Set<Integer> processing=ConcurrentHashMap.newKeySet();
    private final java.util.concurrent.atomic.AtomicBoolean bulkRunning=new java.util.concurrent.atomic.AtomicBoolean();

    @Async
    @Override public void vectorizeDocument(Integer id) {index(id,false);}
    @Async
    @Override public void rebuildVectors(Integer id) {index(id,true);}

    @Async
    @Override public void rebuildAllVectors() {
        if(!bulkRunning.compareAndSet(false,true)) return;
        try {
            for(KnowledgeDocument doc:knowledgeDocumentMapper.selectList(null)) {
                if(documentChunkMapper.selectCount(Wrappers.<DocumentChunk>lambdaQuery()
                        .eq(DocumentChunk::getKnowledgeDocumentId,doc.getId()))>0) index(doc.getId(),true);
            }
        } finally {bulkRunning.set(false);}
    }

    private void index(Integer id,boolean reuseChunks) {
        if(id==null || !processing.add(id)) return;
        List<String> added=new ArrayList<>();
        KnowledgeDocument doc=null;
        try {
            doc=knowledgeDocumentMapper.selectById(id);
            if(doc==null) throw new IllegalArgumentException("文档不存在");
            doc.setVectorStatusType(VectorStatusTypeEnum.处理中.index());knowledgeDocumentMapper.updateById(doc);
            List<DocumentChunk> old=documentChunkMapper.selectList(Wrappers.<DocumentChunk>lambdaQuery()
                    .eq(DocumentChunk::getKnowledgeDocumentId,id).orderByAsc(DocumentChunk::getChunkIndex));
            List<DocumentChunk> prepared=new ArrayList<>();
            if(reuseChunks) prepared.addAll(old);
            else {
                Document parsed=parseDocument(doc.getFileUrl());int position=0;
                for(TextSegment segment:DocumentSplitters.recursive(300,30).split(parsed)) {
                    DocumentChunk chunk=new DocumentChunk();chunk.setKnowledgeDocumentId(id);
                    chunk.setChunkIndex(++position);chunk.setChunkContent(segment.text());
                    chunk.setTokenCount(segment.text().length());chunk.setCreationTime(LocalDateTime.now());prepared.add(chunk);
                }
            }
            if(prepared.isEmpty()) throw new IllegalArgumentException("文档没有可用文本，请先上传原文件");
            List<String> oldIds=old.stream().map(DocumentChunk::getVectorId).filter(VectorServiceImpl::isUuid).toList();
            for(int offset=0;offset<prepared.size();offset+=16) {
                List<TextSegment> segments=prepared.subList(offset,Math.min(offset+16,prepared.size())).stream()
                        .map(c->TextSegment.from(c.getChunkContent(),Metadata.from(Map.of("documentId",id,"chunkIndex",c.getChunkIndex())))).toList();
                var embeddings=embeddingModel.embedAll(segments).content();
                if(embeddings.size()!=segments.size()) throw new IllegalStateException("嵌入服务返回数量不一致");
                var ids=embeddingStore.addAll(embeddings,segments);added.addAll(ids);
                if(ids.size()!=segments.size() || ids.stream().anyMatch(s->!isUuid(s))) throw new IllegalStateException("向量写入结果无效");
            }
            KnowledgeDocument ready=doc;
            transactions.executeWithoutResult(status->{
                if(!reuseChunks) documentChunkMapper.delete(Wrappers.<DocumentChunk>lambdaQuery().eq(DocumentChunk::getKnowledgeDocumentId,id));
                for(int i=0;i<prepared.size();i++) {
                    DocumentChunk chunk=prepared.get(i);chunk.setVectorId(added.get(i));
                    if(reuseChunks) documentChunkMapper.updateById(chunk);else documentChunkMapper.insert(chunk);
                }
                ready.setChunkCount(prepared.size());ready.setVectorStatusType(VectorStatusTypeEnum.已完成.index());
                ready.setVectorTime(LocalDateTime.now());knowledgeDocumentMapper.updateById(ready);
            });
            removeVectors(oldIds);
            log.info("文档向量化完成 id={}, chunks={}",id,prepared.size());
        } catch(Exception e) {
            removeVectors(added);
            if(doc!=null) {doc.setVectorStatusType(VectorStatusTypeEnum.失败.index());knowledgeDocumentMapper.updateById(doc);}
            log.warn("文档向量化失败 id={}, errorType={}",id,e.getClass().getSimpleName());
        } finally {processing.remove(id);}
    }
    @Override public void removeVectors(List<String> ids) {
        List<String> valid=ids.stream().filter(VectorServiceImpl::isUuid).toList();
        if(valid.isEmpty()) return;
        try {embeddingStore.removeAll(valid);} catch(Exception e) {log.warn("旧向量清理待重试 count={}",valid.size());}
    }
    private static boolean isUuid(String id) {
        try {return id!=null && UUID.fromString(id).toString().equalsIgnoreCase(id);}catch(Exception e){return false;}
    }
    private Document parseDocument(String location) throws Exception {
        Path file=LocalDocumentFiles.resolve(location,publicBase);
        try(InputStream in=Files.newInputStream(file)) {return new ApacheTikaDocumentParser().parse(in);}
    }
}
