package com.example.web.service;

import java.util.Map;

/**
 * 心理测评数据统计服务接口
 */
public interface AssessmentStatisticsService {

    /**
     * 获取心理测评统计数据
     */
    Map<String, Object> getAssessmentStatistics();
}
