package com.example.web.service.impl;

import com.example.web.common.*;
import com.example.web.dto.AssessmentRecordDto;
import com.example.web.entity.*;
import com.example.web.enums.RoleTypeEnum;
import com.example.web.mapper.*;
import com.example.web.utils.BaseContext;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OwnershipSecurityTest {
    @BeforeEach void login() {
        var user=new CurrentUserDto();user.setUserId(7);user.setRoleType(RoleTypeEnum.用户);BaseContext.setCurrentUserDto(user);
    }
    @AfterEach void cleanup() {BaseContext.clear();}
    @Test void foreignChatSessionCannotBeReadWrittenStreamedOrRated() {
        var service=new AiChatServiceImpl();var sessions=mock(ChatSessionMapper.class);var messages=mock(ChatMessageMapper.class);
        var feedback=mock(QaFeedbackMapper.class);
        ReflectionTestUtils.setField(service,"chatSessionMapper",sessions);ReflectionTestUtils.setField(service,"chatMessageMapper",messages);
        ReflectionTestUtils.setField(service,"qaFeedbackMapper",feedback);
        var session=new ChatSession();session.setId(10);session.setUserId(8);when(sessions.selectById(10)).thenReturn(session);
        var message=new ChatMessage();message.setChatSessionId(10);when(messages.selectById(20)).thenReturn(message);
        forbidden(()->service.getMessagesBySessionId(10));forbidden(()->service.chat(10,7,"test"));
        var response=new MockHttpServletResponse();forbidden(()->service.chatStream(10,7,"test",response));
        assertEquals(0,response.getContentAsByteArray().length);forbidden(()->service.submitFeedback(7,20,1,"test"));
        verifyNoInteractions(feedback);verify(messages,never()).insert(any(ChatMessage.class));
    }
    @Test void foreignFavoriteCannotBeCancelled() {
        var service=new FavoriteServiceImpl();var mapper=mock(FavoriteMapper.class);ReflectionTestUtils.setField(service,"favoriteMapper",mapper);
        var row=new Favorite();row.setId(10);row.setUserId(8);when(mapper.selectById(10)).thenReturn(row);
        var input=new IdInput();input.setId(10);forbidden(()->service.cancel(input));verify(mapper,never()).deleteById(10);
    }
    @Test void foreignAssessmentCannotBeDeletedAndRawScoresCannotBeWritten() {
        var service=new AssessmentRecordServiceImpl();var mapper=mock(AssessmentRecordMapper.class);ReflectionTestUtils.setField(service,"assessmentRecordMapper",mapper);
        var row=new AssessmentRecord();row.setId(10);row.setUserId(8);when(mapper.selectById(10)).thenReturn(row);
        var input=new IdInput();input.setId(10);forbidden(()->service.deleteById(input));verify(mapper,never()).deleteById(row);
        assertEquals(405,assertThrows(ResponseStatusException.class,()->service.addOrEdit(new AssessmentRecordDto())).getStatusCode().value());
    }
    @Test void foreignCommentCannotBeDeleted() {
        var service=new ArticleCommentServiceImpl();var mapper=mock(ArticleCommentMapper.class);ReflectionTestUtils.setField(service,"articleCommentMapper",mapper);
        var row=new ArticleComment();row.setId(10);row.setUserId(8);when(mapper.selectById(10)).thenReturn(row);
        var input=new IdInput();input.setId(10);forbidden(()->service.deleteById(input));verify(mapper,never()).deleteById(row);
    }
    void forbidden(org.junit.jupiter.api.function.Executable action) {
        assertEquals(403,assertThrows(ResponseStatusException.class,action).getStatusCode().value());
    }
}
