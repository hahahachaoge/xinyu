package com.example.web.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.common.IdInput;
import com.example.web.common.PagedResult;
import com.example.web.dto.ChatMessageDto;
import com.example.web.dto.ChatSessionDto;
import com.example.web.dto.query.ChatSessionPagedInput;
import com.example.web.entity.ChatSession;
import com.example.web.entity.DocumentChunk;
import com.example.web.entity.HotQuestion;
import com.example.web.entity.KnowledgeDocument;
import com.example.web.mapper.ChatMessageMapper;
import com.example.web.mapper.ChatSessionMapper;
import com.example.web.mapper.DocumentChunkMapper;
import com.example.web.mapper.HotQuestionMapper;
import com.example.web.mapper.KnowledgeDocumentMapper;
import com.example.web.service.AiChatService;
import com.example.web.service.ChatSessionService;
import com.example.web.utils.BaseContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI智能问答控制器（用户端）
 */
@RestController
@RequestMapping("/aiChat")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private HotQuestionMapper hotQuestionMapper;

    @Autowired
    private DocumentChunkMapper documentChunkMapper;

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    /**
     * 创建新的对话会话
     */
    @RequestMapping(value = "/createSession", method = RequestMethod.POST)
    public ChatSessionDto createSession(@RequestBody ChatInput input) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        return aiChatService.createSession(userId, input.getQuestion());
    }

    /**
     * 发送消息并获取AI回复（非流式）
     */
    @RequestMapping(value = "/chat", method = RequestMethod.POST)
    public ChatMessageDto chat(@RequestBody ChatInput input) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        return aiChatService.chat(input.getSessionId(), userId, input.getQuestion());
    }

    /**
     * 发送消息并获取AI流式回复（SSE）
     */
    @RequestMapping(value = "/chatStream", method = RequestMethod.POST)
    public void chatStream(@RequestBody ChatInput input, HttpServletResponse response) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        aiChatService.chatStream(input.getSessionId(), userId, input.getQuestion(), response);
    }

    /**
     * 获取当前用户的会话列表
     */
    @RequestMapping(value = "/mySessions", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<ChatSessionDto> mySessions(@RequestBody ChatSessionPagedInput input) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        input.setUserId(userId);
        return chatSessionService.list(input);
    }

    /**
     * 获取会话的所有消息
     */
    @RequestMapping(value = "/getMessages", method = RequestMethod.POST)
    public List<ChatMessageDto> getMessages(@RequestBody IdInput input) {
        return aiChatService.getMessagesBySessionId(input.getId());
    }

    /**
     * 删除对话会话（同时删除关联消息）
     */
    @RequestMapping(value = "/deleteSession", method = RequestMethod.POST)
    public void deleteSession(@RequestBody IdInput input) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        ChatSession session = chatSessionMapper.selectById(input.getId());
        if (session != null) {
            com.example.web.utils.AccessControl.requireOwner(session.getUserId());
            // 删除会话下的所有消息
            chatMessageMapper.delete(
                    Wrappers.lambdaQuery(com.example.web.entity.ChatMessage.class)
                            .eq(com.example.web.entity.ChatMessage::getChatSessionId, input.getId()));
            chatSessionMapper.deleteById(input.getId());
        }
    }

    /**
     * 提交问答反馈
     */
    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public void feedback(@RequestBody FeedbackInput input) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        aiChatService.submitFeedback(userId, input.getChatMessageId(), input.getRating(), input.getFeedbackContent());
    }

    /**
     * 获取热门问题列表
     */
    @RequestMapping(value = "/hotQuestions", method = RequestMethod.POST)
    @SneakyThrows
    public List<HotQuestion> hotQuestions() {
        return hotQuestionMapper.selectList(
                Wrappers.<HotQuestion>lambdaQuery()
                        .orderByDesc(HotQuestion::getAskCount)
                        .last("LIMIT 10"));
    }

    /**
     * 获取知识溯源片段详情
     */
    @RequestMapping(value = "/getChunkDetails", method = RequestMethod.POST)
    @SneakyThrows
    public List<Map<String, Object>> getChunkDetails(@RequestBody ChunkIdsInput input) {
        if (input.getChunkIds() == null || input.getChunkIds().isEmpty()) {
            return List.of();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Integer chunkId : input.getChunkIds()) {
            DocumentChunk chunk = documentChunkMapper.selectById(chunkId);
            if (chunk == null)
                continue;
            Map<String, Object> item = new HashMap<>();
            item.put("chunkId", chunk.getId());
            item.put("chunkIndex", chunk.getChunkIndex());
            item.put("chunkContent", chunk.getChunkContent());
            item.put("tokenCount", chunk.getTokenCount());
            // 查询关联的知识文档
            if (chunk.getKnowledgeDocumentId() != null) {
                KnowledgeDocument doc = knowledgeDocumentMapper.selectById(chunk.getKnowledgeDocumentId());
                if (doc != null) {
                    item.put("docId", doc.getId());
                    item.put("docTitle", doc.getDocTitle());
                    item.put("docDesc", doc.getDocDesc());
                    item.put("fileFormat", doc.getFileFormat());
                }
            }
            result.add(item);
        }
        return result;
    }

    /**
     * 聊天输入模型
     */
    @Data
    public static class ChatInput {
        private Integer sessionId;
        private String question;
    }

    /**
     * 反馈输入模型
     */
    @Data
    public static class FeedbackInput {
        private Integer chatMessageId;
        private Integer rating;
        private String feedbackContent;
    }

    /**
     * 知识片段ID输入
     */
    @Data
    public static class ChunkIdsInput {
        private List<Integer> chunkIds;
    }
}
