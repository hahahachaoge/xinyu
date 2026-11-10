package com.example.web.controller;

import com.example.web.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 用户统计数据
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public Map<String, Object> getUserStatistics() {
        return statisticsService.getUserStatistics();
    }

    /**
     * 问答统计数据
     */
    @RequestMapping(value = "/qa", method = RequestMethod.POST)
    public Map<String, Object> getQaStatistics() {
        return statisticsService.getQaStatistics();
    }

    /**
     * 知识库统计数据
     */
    @RequestMapping(value = "/knowledge", method = RequestMethod.POST)
    public Map<String, Object> getKnowledgeStatistics() {
        return statisticsService.getKnowledgeStatistics();
    }

    /**
     * 问答词云数据
     */
    @RequestMapping(value = "/qaWordCloud", method = RequestMethod.POST)
    public List<Map<String, Object>> getQaWordCloud() {
        return statisticsService.getQaWordCloud();
    }

    /**
     * 知识库词云数据
     */
    @RequestMapping(value = "/knowledgeWordCloud", method = RequestMethod.POST)
    public List<Map<String, Object>> getKnowledgeWordCloud() {
        return statisticsService.getKnowledgeWordCloud();
    }
}
