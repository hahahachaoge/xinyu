package com.example.web.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.dto.AssessmentRecordDto;
import com.example.web.dto.AssessmentSubmitInput;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.utils.AccessControl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AssessmentSubmissionService {
    private final ScaleInfoMapper scales;
    private final ScaleQuestionMapper questions;
    private final ScoreRuleMapper rules;
    private final AssessmentRecordMapper records;
    private final ObjectMapper json;

    @Transactional
    @SneakyThrows
    public AssessmentRecordDto submit(AssessmentSubmitInput input) {
        int userId = AccessControl.requireUser().getUserId();
        if (input == null || input.getScaleInfoId() == null || scales.selectById(input.getScaleInfoId()) == null)
            throw invalid("测评量表不存在");
        List<ScaleQuestion> items = questions.selectList(Wrappers.<ScaleQuestion>lambdaQuery()
                .eq(ScaleQuestion::getScaleInfoId, input.getScaleInfoId())
                .orderByAsc(ScaleQuestion::getQuestionIndex).orderByAsc(ScaleQuestion::getId));
        if (items.isEmpty() || input.getAnswers() == null || input.getAnswers().size() != items.size())
            throw invalid("请完整提交所有题目的答案");
        Map<Integer, Integer> choices = new HashMap<>();
        for (AssessmentSubmitInput.Answer answer : input.getAnswers()) {
            if (answer == null || answer.getQuestionId() == null || answer.getOptionIndex() == null
                    || choices.putIfAbsent(answer.getQuestionId(), answer.getOptionIndex()) != null)
                throw invalid("作答中存在缺失或重复的题目");
        }
        int total = 0;
        List<Map<String, Object>> details = new ArrayList<>();
        for (ScaleQuestion question : items) {
            Integer index = choices.get(question.getId());
            JsonNode options;
            try { options = json.readTree(question.getOptionJson()); }
            catch (Exception badConfig) { throw invalid("量表选项配置无效，请联系管理员"); }
            if (index == null || index < 0 || options == null || !options.isArray() || index >= options.size())
                throw invalid("答案包含不属于该量表的题目或无效选项");
            JsonNode option = options.get(index);
            JsonNode score = option == null ? null : option.get("score");
            if (score == null || !score.isIntegralNumber() || !score.canConvertToInt() || !option.path("label").isTextual())
                throw invalid("量表选项配置无效，请联系管理员");
            try { total = Math.addExact(total, score.intValue()); }
            catch (ArithmeticException overflow) { throw invalid("量表分值配置无效"); }
            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("questionId", question.getId());
            detail.put("questionIndex", question.getQuestionIndex());
            detail.put("optionIndex", index);
            detail.put("selectedLabel", option.get("label").asText());
            detail.put("selectedScore", score.intValue());
            details.add(detail);
        }
        final int calculated = total;
        List<ScoreRule> matches = rules.selectList(Wrappers.<ScoreRule>lambdaQuery()
                .eq(ScoreRule::getScaleInfoId, input.getScaleInfoId())).stream()
                .filter(r -> r.getMinScore() != null && r.getMaxScore() != null
                        && calculated >= r.getMinScore() && calculated <= r.getMaxScore()).toList();
        if (matches.size() != 1) throw invalid("评分规则缺失或重叠，请联系管理员");
        ScoreRule rule = matches.get(0);
        AssessmentRecord record = new AssessmentRecord();
        record.setUserId(userId);
        record.setScaleInfoId(input.getScaleInfoId());
        record.setTotalScore(total);
        record.setResultLabel(rule.getResultLabel());
        record.setResultInterpretation(rule.getResultInterpretation());
        record.setAnswerJson(json.writeValueAsString(details));
        record.setFinishTime(LocalDateTime.now());
        record.setCreationTime(record.getFinishTime());
        records.insert(record);
        AssessmentRecordDto result = record.mapToDto();
        result.setSuggestion(rule.getSuggestion());
        return result;
    }
    private ResponseStatusException invalid(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
