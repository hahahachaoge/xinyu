package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.web.common.CurrentUserDto;
import com.example.web.entity.*;
import com.example.web.enums.RoleTypeEnum;
import com.example.web.mapper.*;
import com.example.web.service.DeepSeekClient;
import com.example.web.utils.BaseContext;
import dev.langchain4j.model.embedding.EmbeddingModel;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChatFailureTest {
    AiChatServiceImpl service;ChatMessageMapper messages;
    @BeforeEach void setup() {
        for(var type:List.of(ChatMessage.class,DocumentChunk.class))
            TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(),""),type);
        service=new AiChatServiceImpl();messages=mock(ChatMessageMapper.class);
        var sessions=mock(ChatSessionMapper.class);var chunks=mock(DocumentChunkMapper.class);var client=mock(DeepSeekClient.class);
        var session=new ChatSession();session.setId(1);session.setUserId(7);
        when(sessions.selectById(1)).thenReturn(session);when(chunks.selectList(any())).thenReturn(List.of());
        when(messages.selectList(any())).thenReturn(List.of());
        when(client.complete(anyList(),any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_GATEWAY,"provider unavailable"));
        ReflectionTestUtils.setField(service,"chatSessionMapper",sessions);ReflectionTestUtils.setField(service,"chatMessageMapper",messages);
        ReflectionTestUtils.setField(service,"documentChunkMapper",chunks);ReflectionTestUtils.setField(service,"deepSeekClient",client);
        ReflectionTestUtils.setField(service,"embeddingModel",mock(EmbeddingModel.class));
        var user=new CurrentUserDto();user.setUserId(7);user.setRoleType(RoleTypeEnum.用户);BaseContext.setCurrentUserDto(user);
    }
    @AfterEach void cleanup(){BaseContext.clear();}
    @Test void failedNonStreamCallDoesNotPersistFakeReplyOrDuplicateUserMessage() {
        assertThrows(ResponseStatusException.class,()->service.chat(1,7,"sleep"));
        verify(messages,never()).insert(any(ChatMessage.class));
    }
    @Test void failedStreamHasErrorWithoutSavedEventOrDoneAndReleasesSessionLock() throws Exception {
        for(int i=0;i<2;i++) {
            var response=new MockHttpServletResponse();service.chatStream(1,7,"sleep",response);
            var text=response.getContentAsString();assertTrue(text.contains("provider unavailable"));
            assertFalse(text.contains("[DONE]"));assertFalse(text.contains("\"type\":\"saved\""));
        }
        verify(messages,never()).insert(any(ChatMessage.class));
    }
}
