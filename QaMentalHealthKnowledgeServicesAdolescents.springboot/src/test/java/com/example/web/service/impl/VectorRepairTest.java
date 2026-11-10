package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import java.util.*;
import java.util.function.Consumer;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.*;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VectorRepairTest {
    KnowledgeDocumentMapper docs;DocumentChunkMapper chunks;EmbeddingModel model;EmbeddingStore<TextSegment> store;
    TransactionTemplate tx;VectorServiceImpl service;KnowledgeDocument doc;DocumentChunk chunk;
    String oldId=UUID.randomUUID().toString(),newId=UUID.randomUUID().toString();
    @BeforeEach @SuppressWarnings("unchecked") void setup() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(),""),DocumentChunk.class);
        docs=mock(KnowledgeDocumentMapper.class);chunks=mock(DocumentChunkMapper.class);model=mock(EmbeddingModel.class);store=mock(EmbeddingStore.class);tx=mock(TransactionTemplate.class);
        doAnswer(i->{((Consumer<TransactionStatus>)i.getArgument(0)).accept(mock(TransactionStatus.class));return null;}).when(tx).executeWithoutResult(any());
        service=new VectorServiceImpl(docs,chunks,model,store,tx);
        doc=new KnowledgeDocument();doc.setId(1);doc.setVectorStatusType(3);doc.setChunkCount(1);
        chunk=new DocumentChunk();chunk.setId(10);chunk.setKnowledgeDocumentId(1);chunk.setChunkIndex(1);chunk.setChunkContent("source text");chunk.setVectorId(oldId);
        when(docs.selectById(1)).thenReturn(doc);when(chunks.selectList(any())).thenReturn(List.of(chunk));
        when(model.embedAll(any())).thenReturn(Response.from(List.of(Embedding.from(new float[]{1,0}))));
        when(store.addAll(any(),any())).thenReturn(List.of(newId));
    }
    @Test void replacesMappingsWithoutChangingChunkIdsAndDeletesOldVectorsAfterCommit() {
        service.rebuildVectors(1);assertEquals(10,chunk.getId());assertEquals(newId,chunk.getVectorId());assertEquals(3,doc.getVectorStatusType());
        var order=inOrder(store,tx);order.verify(store).addAll(any(),any());order.verify(tx).executeWithoutResult(any());order.verify(store).removeAll(List.of(oldId));
        verify(chunks,never()).delete(any());
    }
    @Test void embeddingFailurePreservesOldMappingAndNeverReportsCompleted() {
        when(model.embedAll(any())).thenThrow(new IllegalStateException("provider failed"));
        service.rebuildVectors(1);assertEquals(oldId,chunk.getVectorId());assertEquals(4,doc.getVectorStatusType());
        verify(chunks,never()).updateById(any(DocumentChunk.class));verifyNoInteractions(store);
    }
    @Test void databaseFailureRemovesNewVectorsAndKeepsOldVectors() {
        doThrow(new IllegalStateException("db failed")).when(tx).executeWithoutResult(any());
        service.rebuildVectors(1);assertEquals(4,doc.getVectorStatusType());
        verify(store).removeAll(List.of(newId));verify(store,never()).removeAll(List.of(oldId));
    }
    @Test void missingOriginalFileDoesNotDeleteExistingChunks() {
        doc.setFileUrl("http://localhost:7406/missing/source.txt");service.vectorizeDocument(1);
        assertEquals(4,doc.getVectorStatusType());verify(chunks,never()).delete(any());verifyNoInteractions(model,store);
    }
}
