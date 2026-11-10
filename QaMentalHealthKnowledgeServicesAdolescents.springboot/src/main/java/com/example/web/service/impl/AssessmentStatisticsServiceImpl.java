package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.service.AssessmentStatisticsService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssessmentStatisticsServiceImpl implements AssessmentStatisticsService {

    @Autowired
    private AssessmentRecordMapper assessmentRecordMapper;
    @Autowired
    private ScaleInfoMapper scaleInfoMapper;
    @Autowired
    private ScaleQuestionMapper scaleQuestionMapper;
    @Autowired
    private ScoreRuleMapper scoreRuleMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    @SneakyThrows
    @Override
    public Map<String, Object> getAssessmentStatistics() {
        Map<String, Object> result = new HashMap<>();

        // ========== 卡片汇总 ==========
        Long totalRecords = assessmentRecordMapper.selectCount(Wrappers.<AssessmentRecord>lambdaQuery());
        result.put("totalRecords", totalRecords);

        Long totalScales = scaleInfoMapper.selectCount(Wrappers.<ScaleInfo>lambdaQuery());
        result.put("totalScales", totalScales);

        Long totalQuestions = scaleQuestionMapper.selectCount(Wrappers.<ScaleQuestion>lambdaQuery());
        result.put("totalQuestions", totalQuestions);

        // 参与测评的用户数
        List<AssessmentRecord> allRecords = assessmentRecordMapper.selectList(Wrappers.<AssessmentRecord>lambdaQuery());
        long participantCount = allRecords.stream().map(AssessmentRecord::getUserId).filter(Objects::nonNull).distinct()
                .count();
        result.put("participantCount", participantCount);

        // 平均分
        double avgScore = allRecords.stream().filter(r -> r.getTotalScore() != null)
                .mapToInt(AssessmentRecord::getTotalScore).average().orElse(0);
        result.put("avgScore", Math.round(avgScore * 10) / 10.0);

        // ========== 图表1：各量表测评次数（柱状图） ==========
        List<ScaleInfo> scales = scaleInfoMapper.selectList(Wrappers.<ScaleInfo>lambdaQuery());
        List<Map<String, Object>> scaleCountDist = scales.stream().map(s -> {
            Long count = assessmentRecordMapper.selectCount(
                    Wrappers.<AssessmentRecord>lambdaQuery().eq(AssessmentRecord::getScaleInfoId, s.getId()));
            Map<String, Object> m = new HashMap<>();
            m.put("name", s.getScaleName());
            m.put("count", count);
            return m;
        }).collect(Collectors.toList());
        result.put("scaleCountDist", scaleCountDist);

        // ========== 图表2：结果标签分布（饼图） ==========
        Map<String, Integer> labelCountMap = new HashMap<>();
        for (AssessmentRecord r : allRecords) {
            if (r.getResultLabel() != null && !r.getResultLabel().isEmpty()) {
                labelCountMap.merge(r.getResultLabel(), 1, Integer::sum);
            }
        }
        List<Map<String, Object>> resultLabelDist = labelCountMap.entrySet().stream().map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("name", e.getKey());
            m.put("value", e.getValue());
            return m;
        }).collect(Collectors.toList());
        result.put("resultLabelDist", resultLabelDist);

        // ========== 图表3：近7天测评趋势（折线图） ==========
        List<Map<String, Object>> assessTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            Long count = assessmentRecordMapper.selectCount(Wrappers.<AssessmentRecord>lambdaQuery()
                    .ge(AssessmentRecord::getFinishTime, start).le(AssessmentRecord::getFinishTime, end));
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", count);
            assessTrend.add(item);
        }
        result.put("assessTrend", assessTrend);

        // ========== 图表4：各量表平均分对比（雷达图） ==========
        List<Map<String, Object>> scaleAvgScores = scales.stream().map(s -> {
            List<AssessmentRecord> scaleRecords = allRecords.stream()
                    .filter(r -> s.getId().equals(r.getScaleInfoId()) && r.getTotalScore() != null)
                    .collect(Collectors.toList());
            double avg = scaleRecords.stream().mapToInt(AssessmentRecord::getTotalScore).average().orElse(0);
            int maxPossible = s.getQuestionCount() != null ? s.getQuestionCount() * 3 : 15;
            Map<String, Object> m = new HashMap<>();
            m.put("name", s.getScaleName());
            m.put("avg", Math.round(avg * 10) / 10.0);
            m.put("max", maxPossible);
            return m;
        }).collect(Collectors.toList());
        result.put("scaleAvgScores", scaleAvgScores);

        // ========== 图表5：分数段分布（柱状图） ==========
        int[] scoreBuckets = new int[] { 0, 0, 0, 0 }; // 0-3, 4-6, 7-9, 10+
        for (AssessmentRecord r : allRecords) {
            if (r.getTotalScore() == null)
                continue;
            int s = r.getTotalScore();
            if (s <= 3)
                scoreBuckets[0]++;
            else if (s <= 6)
                scoreBuckets[1]++;
            else if (s <= 9)
                scoreBuckets[2]++;
            else
                scoreBuckets[3]++;
        }
        List<Map<String, Object>> scoreRangeDist = new ArrayList<>();
        String[] rangeLabels = { "0-3分(良好)", "4-6分(一般)", "7-9分(偏高)", "10分以上(严重)" };
        String[] rangeColors = { "#6BBF73", "#5B9BD5", "#F0A75B", "#E8726D" };
        for (int i = 0; i < 4; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", rangeLabels[i]);
            m.put("count", scoreBuckets[i]);
            m.put("color", rangeColors[i]);
            scoreRangeDist.add(m);
        }
        result.put("scoreRangeDist", scoreRangeDist);

        // ========== 图表6：测评参与用户TOP5（横向柱状图） ==========
        Map<Integer, Long> userCountMap = allRecords.stream()
                .filter(r -> r.getUserId() != null)
                .collect(Collectors.groupingBy(AssessmentRecord::getUserId, Collectors.counting()));
        List<Map<String, Object>> topUsers = userCountMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    SysUser user = sysUserMapper.selectById(e.getKey());
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", user != null ? (user.getNickName() != null ? user.getNickName() : user.getUserName())
                            : "用户" + e.getKey());
                    m.put("count", e.getValue());
                    return m;
                }).collect(Collectors.toList());
        result.put("topUsers", topUsers);

        return result;
    }
}
