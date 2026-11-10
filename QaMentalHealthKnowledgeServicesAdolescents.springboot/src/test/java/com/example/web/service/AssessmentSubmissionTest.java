package com.example.web.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.web.common.CurrentUserDto;
import com.example.web.dto.AssessmentSubmitInput;
import com.example.web.entity.*;
import com.example.web.enums.RoleTypeEnum;
import com.example.web.mapper.*;
import com.example.web.utils.BaseContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.*;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssessmentSubmissionTest {
    final ScaleInfoMapper scales = mock(ScaleInfoMapper.class);
    final ScaleQuestionMapper questions = mock(ScaleQuestionMapper.class);
    final ScoreRuleMapper rules = mock(ScoreRuleMapper.class);
    final AssessmentRecordMapper records = mock(AssessmentRecordMapper.class);
    final ObjectMapper json = new ObjectMapper();
    final AssessmentSubmissionService service = new AssessmentSubmissionService(scales, questions, rules, records, json);
    ScaleQuestion question;
    ScoreRule rule;

    @BeforeEach void setup() {
        for (Class<?> type : List.of(ScaleQuestion.class, ScoreRule.class))
            TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), type);
        CurrentUserDto user = new CurrentUserDto(); user.setUserId(7); user.setRoleType(RoleTypeEnum.用户);
        BaseContext.setCurrentUserDto(user);
        question = new ScaleQuestion(); question.setId(11); question.setQuestionIndex(1); question.setScaleInfoId(3);
        question.setOptionJson("[{\"label\":\"A\",\"score\":0},{\"label\":\"B\",\"score\":3}]");
        rule = new ScoreRule(); rule.setMinScore(3); rule.setMaxScore(3); rule.setResultLabel("server label");
        rule.setResultInterpretation("server interpretation"); rule.setSuggestion("server suggestion");
        when(scales.selectById(3)).thenReturn(new ScaleInfo());
        when(questions.selectList(any())).thenReturn(List.of(question));
        when(rules.selectList(any())).thenReturn(List.of(rule));
    }
    @AfterEach void cleanup() { BaseContext.clear(); }
    AssessmentSubmitInput input(int questionId, int option) {
        AssessmentSubmitInput input = new AssessmentSubmitInput(); input.setScaleInfoId(3);
        AssessmentSubmitInput.Answer answer = new AssessmentSubmitInput.Answer();
        answer.setQuestionId(questionId); answer.setOptionIndex(option); input.setAnswers(List.of(answer)); return input;
    }
    @Test void calculatesFromStoredOptionsAndRuleWithInclusiveBoundary() throws Exception {
        var result = service.submit(input(11, 1));
        assertEquals(3, result.getTotalScore()); assertEquals(7, result.getUserId());
        assertEquals("server label", result.getResultLabel()); assertEquals("server suggestion", result.getSuggestion());
        assertNotNull(result.getFinishTime());
        var snapshot = json.readTree(result.getAnswerJson()).get(0);
        assertEquals(11, snapshot.get("questionId").asInt()); assertEquals("B", snapshot.get("selectedLabel").asText());
        verify(records).insert(any(AssessmentRecord.class));
    }
    @Test void clientCannotChooseOwnerScoreOrResult() throws Exception {
        var input=json.readValue("{\"scaleInfoId\":3,\"userId\":999,\"totalScore\":999,\"resultLabel\":\"forged\",\"finishTime\":\"2000-01-01 00:00:00\",\"answers\":[{\"questionId\":11,\"optionIndex\":1}]}",AssessmentSubmitInput.class);
        var result=service.submit(input);assertEquals(7,result.getUserId());assertEquals(3,result.getTotalScore());assertEquals("server label",result.getResultLabel());
    }
    @Test void missingAnswerIsRejectedWithoutSaving() {
        var input = input(11,1); input.setAnswers(List.of()); rejects(input);
    }
    @Test void foreignQuestionIsRejectedWithoutSaving() { rejects(input(99,1)); }
    @Test void negativeOptionIsRejectedWithoutSaving() { rejects(input(11,-1)); }
    @Test void outOfRangeOptionIsRejectedWithoutSaving() { rejects(input(11,2)); }
    @Test void duplicateQuestionIsRejectedWithoutSaving() {
        when(questions.selectList(any())).thenReturn(List.of(question, question));
        var input = input(11,1); input.setAnswers(List.of(input.getAnswers().get(0),input.getAnswers().get(0))); rejects(input);
    }
    @Test void overlappingRulesAreRejectedWithoutSaving() {
        when(rules.selectList(any())).thenReturn(List.of(rule,rule)); rejects(input(11,1));
    }
    @Test void missingRuleIsRejectedWithoutSaving() { rule.setMinScore(4); rejects(input(11,1)); }
    @Test void invalidStoredOptionIsRejectedWithoutSaving() { question.setOptionJson("[null]"); rejects(input(11,0)); }
    @Test void unauthenticatedSubmissionIsRejected() {
        BaseContext.clear(); var error=assertThrows(ResponseStatusException.class,()->service.submit(input(11,1)));
        assertEquals(401,error.getStatusCode().value()); verifyNoInteractions(records);
    }
    private void rejects(AssessmentSubmitInput input) {
        var error=assertThrows(ResponseStatusException.class,()->service.submit(input));
        assertEquals(400,error.getStatusCode().value()); verifyNoInteractions(records);
    }
}
