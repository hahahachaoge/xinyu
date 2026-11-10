package com.example.web.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.entity.DocumentChunk;
import com.example.web.mapper.DocumentChunkMapper;
import com.example.web.mapper.KnowledgeDocumentMapper;
import java.sql.DriverManager;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VectorIndexAudit {
    private final DocumentChunkMapper chunks;
    private final KnowledgeDocumentMapper documents;
    @Value("${langchain4j.pgvector.host}") private String host;
    @Value("${langchain4j.pgvector.port}") private int port;
    @Value("${langchain4j.pgvector.database}") private String database;
    @Value("${langchain4j.pgvector.user}") private String user;
    @Value("${langchain4j.pgvector.password}") private String password;
    @Value("${langchain4j.pgvector.table}") private String table;
    @Value("${langchain4j.pgvector.dimension}") private int dimension;
    public VectorIndexAudit(DocumentChunkMapper chunks,KnowledgeDocumentMapper documents) {this.chunks=chunks;this.documents=documents;}

    public Map<String,Object> inspect() {
        if(!table.matches("[A-Za-z_][A-Za-z0-9_]*")) throw new IllegalStateException("向量表配置无效");
        Map<String,String> vectors=new HashMap<>();int wrongDimension=0;
        try(var connection=DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+database,user,password);
            var statement=connection.createStatement();
            var rows=statement.executeQuery("SELECT embedding_id::text, text, vector_dims(embedding) FROM \""+table+"\"")) {
            while(rows.next()) {vectors.put(rows.getString(1),rows.getString(2));if(rows.getInt(3)!=dimension)wrongDimension++;}
        } catch(Exception error) {throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"无法检查向量库，请检查 PostgreSQL 配置");}
        List<DocumentChunk> items=chunks.selectList(Wrappers.<DocumentChunk>lambdaQuery());
        List<Integer> missing=new ArrayList<>(),mismatched=new ArrayList<>();Set<String> referenced=new HashSet<>();
        for(DocumentChunk chunk:items) {
            if(!vectors.containsKey(chunk.getVectorId())) missing.add(chunk.getId());
            else {referenced.add(chunk.getVectorId());if(!Objects.equals(vectors.get(chunk.getVectorId()),chunk.getChunkContent()))mismatched.add(chunk.getId());}
        }
        var docs=documents.selectList(null);
        var pending=docs.stream().filter(d->!Integer.valueOf(3).equals(d.getVectorStatusType())).map(d->d.getId()).toList();
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("chunkCount",items.size());result.put("vectorCount",vectors.size());result.put("matchedCount",referenced.size());
        result.put("missingChunkIds",missing);result.put("mismatchedChunkIds",mismatched);result.put("wrongDimensionCount",wrongDimension);
        result.put("orphanVectorCount",vectors.size()-referenced.size());result.put("pendingDocumentIds",pending);
        result.put("healthy",!items.isEmpty() && missing.isEmpty() && mismatched.isEmpty() && wrongDimension==0 && vectors.size()==referenced.size());
        return result;
    }
}
