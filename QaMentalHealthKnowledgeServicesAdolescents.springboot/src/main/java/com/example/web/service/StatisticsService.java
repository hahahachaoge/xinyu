package com.example.web.service;

import java.util.List;
import java.util.Map;

/**
 * 数据统计服务接口
 */
public interface StatisticsService {

    /** 用户统计 */
    Map<String, Object> getUserStatistics();

    /** 问答统计 */
    Map<String, Object> getQaStatistics();

    /** 知识库统计 */
    Map<String, Object> getKnowledgeStatistics();

    /** 问答词云数据（分词） */
    List<Map<String, Object>> getQaWordCloud();

    /** 知识库词云数据（分词） */
    List<Map<String, Object>> getKnowledgeWordCloud();
}
