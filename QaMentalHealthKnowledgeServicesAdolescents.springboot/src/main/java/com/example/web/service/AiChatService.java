package com.example.web.service;

import com.example.web.dto.ChatMessageDto;
import com.example.web.dto.ChatSessionDto;
import com.example.web.entity.DocumentChunk;

import java.util.List;

/**
 * AI智能问答服务接口
 */
public interface AiChatService {

    /**
     * 创建新的对话会话
     */
    ChatSessionDto createSession(Integer userId, String firstMessage);

    /**
     * 发送消息并获取AI回复（非流式）
     */
    ChatMessageDto chat(Integer sessionId, Integer userId, String question);

    /**
     * 发送消息并获取AI流式回复（SSE）
     */
    void chatStream(Integer sessionId, Integer userId, String question,
            jakarta.servlet.http.HttpServletResponse response);

    /**
     * 获取会话的所有消息
     */
    List<ChatMessageDto> getMessagesBySessionId(Integer sessionId);

    /**
     * 检索相关知识片段
     */
    List<DocumentChunk> retrieveChunks(String question, int topK);

    /**
     * 提交问答反馈
     */
    void submitFeedback(Integer userId, Integer chatMessageId, Integer rating, String feedbackContent);
}
