package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.entity.DocumentChunk;
import com.example.web.entity.KnowledgeDocument;
import com.example.web.enums.VectorStatusTypeEnum;
import com.example.web.exception.CustomException;
import com.example.web.mapper.DocumentChunkMapper;
import com.example.web.mapper.KnowledgeDocumentMapper;
import com.example.web.service.VectorService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 向量化服务实现类
 * 负责文档的文本分块与向量化处理
 */
@Slf4j
@Service
public class VectorServiceImpl implements VectorService {

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    private DocumentChunkMapper documentChunkMapper;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired(required = false)
    private EmbeddingStore<TextSegment> embeddingStore;

    /**
     * 异步执行文档向量化
     */
    @Async
    @Override
    public void vectorizeDocument(Integer knowledgeDocumentId) {
        KnowledgeDocument doc = knowledgeDocumentMapper.selectById(knowledgeDocumentId);
        if (doc == null) {
            log.error("知识文档不存在, id={}", knowledgeDocumentId);
            return;
        }

        try {
            // 1. 更新状态为处理中
            doc.setVectorStatusType(VectorStatusTypeEnum.处理中.index());
            knowledgeDocumentMapper.updateById(doc);

            // 2. 删除旧的分块数据
            documentChunkMapper.delete(Wrappers.<DocumentChunk>lambdaQuery()
                    .eq(DocumentChunk::getKnowledgeDocumentId, knowledgeDocumentId));

            // 3. 解析文档内容
            String fileUrl = doc.getFileUrl();
            if (fileUrl == null || fileUrl.isEmpty()) {
                throw new CustomException("文件路径为空，无法向量化");
            }

            Document document = parseDocument(fileUrl);

            // 4. 文本分块（每块约300个字符，重叠30个字符）
            List<TextSegment> segments = DocumentSplitters
                    .recursive(300, 30)
                    .split(document);

            // 5. 逐块生成向量并存储
            int chunkIndex = 0;
            for (TextSegment segment : segments) {
                chunkIndex++;
                String content = segment.text();

                // 生成向量嵌入
                Embedding embedding = embeddingModel.embed(content).content();

                // 存入向量数据库
                String vectorId = null;
                if (embeddingStore != null) {
                    vectorId = embeddingStore.add(embedding, segment);
                }

                // 存入MySQL分块表
                DocumentChunk chunk = new DocumentChunk();
                chunk.setKnowledgeDocumentId(knowledgeDocumentId);
                chunk.setChunkIndex(chunkIndex);
                chunk.setChunkContent(content);
                chunk.setTokenCount(content.length());
                chunk.setVectorId(vectorId);
                chunk.setCreationTime(LocalDateTime.now());
                documentChunkMapper.insert(chunk);
            }

            // 6. 更新文档状态为已完成
            doc.setChunkCount(chunkIndex);
            doc.setVectorStatusType(VectorStatusTypeEnum.已完成.index());
            doc.setVectorTime(LocalDateTime.now());
            knowledgeDocumentMapper.updateById(doc);

            log.info("文档向量化完成, id={}, 分块数={}", knowledgeDocumentId, chunkIndex);

        } catch (Exception e) {
            log.error("文档向量化失败, id={}", knowledgeDocumentId, e);
            // 更新状态为失败
            doc.setVectorStatusType(VectorStatusTypeEnum.失败.index());
            knowledgeDocumentMapper.updateById(doc);
        }
    }

    /**
     * 解析文档文件
     */
    private Document parseDocument(String fileUrl) throws Exception {
        DocumentParser parser = new ApacheTikaDocumentParser();
        InputStream inputStream;

        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
            // 从URL路径解析出本地文件路径
            // fileUrl格式: http://localhost:7406/123456789/filename.pdf
            String relativePath = fileUrl.replaceFirst("http://[^/]+/", "");
            String localPath = System.getProperty("user.dir") + "/external-resources/" + relativePath;
            File file = new File(localPath);
            if (!file.exists()) {
                // 尝试直接从URL下载
                inputStream = new URL(fileUrl).openStream();
            } else {
                inputStream = new FileInputStream(file);
            }
        } else {
            inputStream = new FileInputStream(new File(fileUrl));
        }

        return parser.parse(inputStream);
    }
}
