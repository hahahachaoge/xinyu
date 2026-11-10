package com.example.web.service;

/**
 * 向量化服务接口
 */
public interface VectorService {

    /**
     * 对知识文档执行文本分块与向量化处理
     * 
     * @param knowledgeDocumentId 知识文档ID
     */
    void vectorizeDocument(Integer knowledgeDocumentId);
    void rebuildVectors(Integer knowledgeDocumentId);
    void rebuildAllVectors();
    void removeVectors(java.util.List<String> ids);
}
