package com.example.web.controller;

import com.example.web.service.AssessmentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 心理测评数据统计控制器
 */
@RestController
@RequestMapping("/assessmentStatistics")
public class AssessmentStatisticsController {

    @Autowired
    private AssessmentStatisticsService assessmentStatisticsService;

    /**
     * 获取心理测评统计数据
     */
    @RequestMapping(value = "/data", method = RequestMethod.POST)
    public Map<String, Object> getData() {
        return assessmentStatisticsService.getAssessmentStatistics();
    }
}
