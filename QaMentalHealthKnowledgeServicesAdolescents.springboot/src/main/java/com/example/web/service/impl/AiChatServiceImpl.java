package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.config.DeepSeekConfig;
import com.example.web.dto.ChatMessageDto;
import com.example.web.dto.ChatSessionDto;
import com.example.web.entity.*;
import com.example.web.enums.SenderRoleEnum;
import com.example.web.exception.CustomException;
import com.example.web.mapper.*;
import com.example.web.service.AiChatService;
import com.example.web.utils.Extension;
import com.example.web.utils.AccessControl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI智能问答服务实现类
 * 基于RAG技术：检索知识库 + 调用大模型生成回答
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private DocumentChunkMapper documentChunkMapper;

    @Autowired
    private QaFeedbackMapper qaFeedbackMapper;

    @Autowired
    private HotQuestionMapper hotQuestionMapper;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired(required = false)
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private DeepSeekConfig deepSeekConfig;

    private ChatSession requireSession(Integer sessionId) {
        ChatSession session = sessionId == null ? null : chatSessionMapper.selectById(sessionId);
        if (session == null) throw new CustomException("会话不存在");
        AccessControl.requireOwner(session.getUserId());
        return session;
    }

    private static final String SYSTEM_PROMPT = "你是一个专业的青少年心理健康咨询助手。你的回答应该：\n" +
            "1. 温暖、专业、有同理心，适合青少年阅读\n" +
            "2. 基于提供的知识库内容进行回答，不要编造信息\n" +
            "3. 如果知识库中没有相关内容，可以基于心理学常识给出建议，但要说明\n" +
            "4. 对于严重的心理问题，建议寻求专业心理咨询师帮助\n" +
            "5. 回答要有条理，适当使用分点说明\n" +
            "6. 语气亲切自然，像一个值得信赖的朋友";

    /**
     * 创建新的对话会话
     */
    @SneakyThrows
    @Override
    public ChatSessionDto createSession(Integer userId, String firstMessage) {
        userId = AccessControl.requireUser().getUserId();
        if (firstMessage == null || firstMessage.isBlank()) throw new CustomException("问题不能为空");
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        // 用第一条消息的前20个字作为标题
        String title = firstMessage.length() > 20 ? firstMessage.substring(0, 20) + "..." : firstMessage;
        session.setSessionTitle(title);
        session.setMessageCount(0);
        session.setLastMessageTime(LocalDateTime.now());
        session.setCreationTime(LocalDateTime.now());
        chatSessionMapper.insert(session);

        ChatSessionDto dto = new ChatSessionDto();
        BeanUtils.copyProperties(dto, session);
        return dto;
    }

    /**
     * 发送消息并获取AI回复（非流式）
     */
    @SneakyThrows
    @Override
    public ChatMessageDto chat(Integer sessionId, Integer userId, String question) {
        ChatSession session = requireSession(sessionId);

        // 1. 保存用户消息
        ChatMessage userMsg = new ChatMessage();
        userMsg.setChatSessionId(sessionId);
        userMsg.setSenderRole(SenderRoleEnum.用户.index());
        userMsg.setMessageContent(question);
        userMsg.setCreationTime(LocalDateTime.now());
        chatMessageMapper.insert(userMsg);

        // 2. RAG检索相关知识片段
        List<DocumentChunk> chunks = retrieveChunks(question, 3);
        String chunkIds = chunks.stream()
                .map(c -> String.valueOf(c.getId()))
                .collect(Collectors.joining(","));
        String knowledgeContext = chunks.stream()
                .map(DocumentChunk::getChunkContent)
                .collect(Collectors.joining("\n\n"));

        // 3. 获取历史对话上下文（最近6条消息）
        List<ChatMessage> history = chatMessageMapper.selectList(
                Wrappers.<ChatMessage>lambdaQuery()
                        .eq(ChatMessage::getChatSessionId, sessionId)
                        .orderByDesc(ChatMessage::getCreationTime)
                        .last("LIMIT 6"));
        // 反转为时间正序
        List<ChatMessage> historyAsc = new ArrayList<>(history);
        java.util.Collections.reverse(historyAsc);

        // 4. 构建Prompt并调用大模型
        String aiReply = callDeepSeekApi(question, knowledgeContext, historyAsc);

        // 5. 保存AI回复消息
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setChatSessionId(sessionId);
        aiMsg.setSenderRole(SenderRoleEnum.系统.index());
        aiMsg.setMessageContent(aiReply);
        aiMsg.setRetrievedChunkIds(chunkIds.isEmpty() ? null : chunkIds);
        aiMsg.setCreationTime(LocalDateTime.now());
        chatMessageMapper.insert(aiMsg);

        // 6. 更新会话信息
        Long msgCount = chatMessageMapper.selectCount(
                Wrappers.<ChatMessage>lambdaQuery().eq(ChatMessage::getChatSessionId, sessionId));
        session.setMessageCount(msgCount.intValue());
        session.setLastMessageTime(LocalDateTime.now());
        chatSessionMapper.updateById(session);

        // 7. 更新热门问题计数
        updateHotQuestionCount(question);

        ChatMessageDto dto = new ChatMessageDto();
        BeanUtils.copyProperties(dto, aiMsg);
        return dto;
    }

    /**
     * 流式对话（SSE）
     */
    @Override
    public void chatStream(Integer sessionId, Integer userId, String question, HttpServletResponse response) {
        ChatSession session = requireSession(sessionId);
        try {
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Connection", "keep-alive");

            // 1. 保存用户消息
            ChatMessage userMsg = new ChatMessage();
            userMsg.setChatSessionId(sessionId);
            userMsg.setSenderRole(SenderRoleEnum.用户.index());
            userMsg.setMessageContent(question);
            userMsg.setCreationTime(LocalDateTime.now());
            chatMessageMapper.insert(userMsg);

            // 2. RAG检索
            List<DocumentChunk> chunks = retrieveChunks(question, 3);
            String chunkIds = chunks.stream()
                    .map(c -> String.valueOf(c.getId()))
                    .collect(Collectors.joining(","));
            String knowledgeContext = chunks.stream()
                    .map(DocumentChunk::getChunkContent)
                    .collect(Collectors.joining("\n\n"));

            // 3. 获取历史上下文
            List<ChatMessage> history = chatMessageMapper.selectList(
                    Wrappers.<ChatMessage>lambdaQuery()
                            .eq(ChatMessage::getChatSessionId, sessionId)
                            .orderByDesc(ChatMessage::getCreationTime)
                            .last("LIMIT 6"));
            List<ChatMessage> historyAsc = new ArrayList<>(history);
            java.util.Collections.reverse(historyAsc);

            // 4. 流式调用DeepSeek API
            StringBuilder fullReply = new StringBuilder();
            PrintWriter writer = response.getWriter();

            // 先发送检索到的知识片段ID
            if (!chunkIds.isEmpty()) {
                writer.write("data: {\"type\":\"chunks\",\"chunkIds\":\"" + chunkIds + "\"}\n\n");
                writer.flush();
            }

            callDeepSeekStream(question, knowledgeContext, historyAsc, writer, fullReply);

            // 发送结束标记
            writer.write("data: [DONE]\n\n");
            writer.flush();

            // 5. 保存完整的AI回复
            ChatMessage aiMsg = new ChatMessage();
            aiMsg.setChatSessionId(sessionId);
            aiMsg.setSenderRole(SenderRoleEnum.系统.index());
            aiMsg.setMessageContent(fullReply.toString());
            aiMsg.setRetrievedChunkIds(chunkIds.isEmpty() ? null : chunkIds);
            aiMsg.setCreationTime(LocalDateTime.now());
            chatMessageMapper.insert(aiMsg);

            // 6. 更新会话
            Long msgCount = chatMessageMapper.selectCount(
                    Wrappers.<ChatMessage>lambdaQuery().eq(ChatMessage::getChatSessionId, sessionId));
            session.setMessageCount(msgCount.intValue());
            session.setLastMessageTime(LocalDateTime.now());
            chatSessionMapper.updateById(session);

            // 7. 更新热门问题
            updateHotQuestionCount(question);

            // 发送保存的消息ID
            writer.write("data: {\"type\":\"saved\",\"messageId\":" + aiMsg.getId() + "}\n\n");
            writer.flush();

        } catch (Exception e) {
            log.error("流式对话异常", e);
            try {
                PrintWriter writer = response.getWriter();
                writer.write("data: {\"error\":\"" + e.getMessage() + "\"}\n\n");
                writer.flush();
            } catch (Exception ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }

    /**
     * 获取会话的所有消息
     */
    @SneakyThrows
    @Override
    public List<ChatMessageDto> getMessagesBySessionId(Integer sessionId) {
        requireSession(sessionId);
        List<ChatMessage> messages = chatMessageMapper.selectList(
                Wrappers.<ChatMessage>lambdaQuery()
                        .eq(ChatMessage::getChatSessionId, sessionId)
                        .orderByAsc(ChatMessage::getCreationTime));
        return Extension.copyBeanList(messages, ChatMessageDto.class);
    }

    /**
     * 检索相关知识片段
     */
    @Override
    public List<DocumentChunk> retrieveChunks(String question, int topK) {
        try {
            if (embeddingStore == null) {
                // 向量库不可用时，使用关键词模糊匹配
                return documentChunkMapper.selectList(
                        Wrappers.<DocumentChunk>lambdaQuery()
                                .like(DocumentChunk::getChunkContent,
                                        question.length() > 10 ? question.substring(0, 10) : question)
                                .last("LIMIT " + topK));
            }

            // 向量检索
            Embedding queryEmbedding = embeddingModel.embed(question).content();
            List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(queryEmbedding, topK, 0.5);

            if (matches.isEmpty()) {
                // 向量检索无结果时回退到关键词匹配
                return documentChunkMapper.selectList(
                        Wrappers.<DocumentChunk>lambdaQuery()
                                .like(DocumentChunk::getChunkContent,
                                        question.length() > 6 ? question.substring(0, 6) : question)
                                .last("LIMIT " + topK));
            }

            // 通过vectorId查找对应的DocumentChunk
            List<String> vectorIds = matches.stream()
                    .map(m -> m.embeddingId())
                    .collect(Collectors.toList());

            return documentChunkMapper.selectList(
                    Wrappers.<DocumentChunk>lambdaQuery()
                            .in(DocumentChunk::getVectorId, vectorIds));
        } catch (Exception e) {
            log.warn("向量检索异常，回退到关键词匹配: {}", e.getMessage());
            return documentChunkMapper.selectList(
                    Wrappers.<DocumentChunk>lambdaQuery()
                            .like(DocumentChunk::getChunkContent,
                                    question.length() > 6 ? question.substring(0, 6) : question)
                            .last("LIMIT " + topK));
        }
    }

    /**
     * 提交问答反馈
     */
    @SneakyThrows
    @Override
    public void submitFeedback(Integer userId, Integer chatMessageId, Integer rating, String feedbackContent) {
        userId = AccessControl.requireUser().getUserId();
        ChatMessage message = chatMessageMapper.selectById(chatMessageId);
        if (message == null) throw new CustomException("消息不存在");
        requireSession(message.getChatSessionId());
        // 检查是否已反馈
        Long count = qaFeedbackMapper.selectCount(
                Wrappers.<QaFeedback>lambdaQuery()
                        .eq(QaFeedback::getUserId, userId)
                        .eq(QaFeedback::getChatMessageId, chatMessageId));
        if (count > 0) {
            // 更新已有反馈
            QaFeedback existing = qaFeedbackMapper.selectOne(
                    Wrappers.<QaFeedback>lambdaQuery()
                            .eq(QaFeedback::getUserId, userId)
                            .eq(QaFeedback::getChatMessageId, chatMessageId));
            existing.setRating(rating);
            existing.setFeedbackContent(feedbackContent);
            qaFeedbackMapper.updateById(existing);
            return;
        }

        QaFeedback feedback = new QaFeedback();
        feedback.setUserId(userId);
        feedback.setChatMessageId(chatMessageId);
        feedback.setRating(rating);
        feedback.setFeedbackContent(feedbackContent);
        feedback.setCreationTime(LocalDateTime.now());
        qaFeedbackMapper.insert(feedback);
    }

    /**
     * 调用DeepSeek API（非流式）
     */
    private String callDeepSeekApi(String question, String knowledgeContext, List<ChatMessage> history) {
        try {
            String requestBody = buildRequestBody(question, knowledgeContext, history, false);

            HttpURLConnection conn = (HttpURLConnection) new URL(deepSeekConfig.getCompletionsUrl()).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + deepSeekConfig.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(120000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(sb.toString());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            log.error("调用DeepSeek API失败", e);
            return "抱歉，我暂时无法回答你的问题。请稍后再试，或者联系心理老师获取帮助。";
        }
    }

    /**
     * 调用DeepSeek API（流式SSE）
     */
    private void callDeepSeekStream(String question, String knowledgeContext, List<ChatMessage> history,
            PrintWriter writer, StringBuilder fullReply) {
        try {
            String requestBody = buildRequestBody(question, knowledgeContext, history, true);

            HttpURLConnection conn = (HttpURLConnection) new URL(deepSeekConfig.getCompletionsUrl()).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + deepSeekConfig.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(120000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            ObjectMapper mapper = new ObjectMapper();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6).trim();
                    if ("[DONE]".equals(data))
                        break;

                    try {
                        JsonNode node = mapper.readTree(data);
                        String content = node.path("choices").get(0).path("delta").path("content").asText("");
                        if (!content.isEmpty()) {
                            fullReply.append(content);
                            // 转义JSON特殊字符
                            String escaped = content.replace("\\", "\\\\")
                                    .replace("\"", "\\\"")
                                    .replace("\n", "\\n")
                                    .replace("\r", "\\r")
                                    .replace("\t", "\\t");
                            writer.write("data: {\"type\":\"content\",\"content\":\"" + escaped + "\"}\n\n");
                            writer.flush();
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            reader.close();

        } catch (Exception e) {
            log.error("流式调用DeepSeek API失败", e);
            String fallback = "抱歉，我暂时无法回答你的问题。请稍后再试。";
            fullReply.append(fallback);
            writer.write("data: {\"type\":\"content\",\"content\":\"" + fallback + "\"}\n\n");
            writer.flush();
        }
    }

    /**
     * 构建请求体JSON
     */
    private String buildRequestBody(String question, String knowledgeContext, List<ChatMessage> history,
            boolean stream) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"model\":").append(new ObjectMapper().valueToTree(deepSeekConfig.getModelName()).toString())
                .append(",\"stream\":").append(stream)
                .append(",\"temperature\":0.7,\"max_tokens\":2000,\"messages\":[");

        // System prompt
        String systemContent = SYSTEM_PROMPT;
        if (Extension.isNotNullOrEmpty(knowledgeContext)) {
            systemContent += "\n\n以下是从知识库中检索到的相关内容，请基于这些内容回答用户的问题：\n\n" + knowledgeContext;
        }
        sb.append("{\"role\":\"system\",\"content\":").append(escapeJson(systemContent)).append("}");

        // 历史消息（排除当前刚插入的用户消息，因为它已在history中）
        for (ChatMessage msg : history) {
            String role = msg.getSenderRole() == SenderRoleEnum.用户.index() ? "user" : "assistant";
            sb.append(",{\"role\":\"").append(role).append("\",\"content\":")
                    .append(escapeJson(msg.getMessageContent())).append("}");
        }

        // 当前用户问题（如果history中最后一条不是当前问题）
        if (history.isEmpty() || !history.get(history.size() - 1).getMessageContent().equals(question)) {
            sb.append(",{\"role\":\"user\",\"content\":").append(escapeJson(question)).append("}");
        }

        sb.append("]}");
        return sb.toString();
    }

    /**
     * JSON字符串转义
     */
    private String escapeJson(String text) {
        if (text == null)
            return "\"\"";
        return "\"" + text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }

    /**
     * 更新热门问题计数
     */
    private void updateHotQuestionCount(String question) {
        try {
            // 查找是否有相似的热门问题
            List<HotQuestion> hotList = hotQuestionMapper.selectList(
                    Wrappers.<HotQuestion>lambdaQuery()
                            .like(HotQuestion::getQuestionText,
                                    question.length() > 8 ? question.substring(0, 8) : question));
            if (!hotList.isEmpty()) {
                HotQuestion hot = hotList.get(0);
                hot.setAskCount(hot.getAskCount() != null ? hot.getAskCount() + 1 : 1);
                hotQuestionMapper.updateById(hot);
            }
        } catch (Exception e) {
            log.warn("更新热门问题计数失败: {}", e.getMessage());
        }
    }
}
