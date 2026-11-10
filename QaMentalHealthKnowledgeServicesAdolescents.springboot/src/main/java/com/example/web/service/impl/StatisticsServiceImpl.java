package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.entity.*;
import com.example.web.enums.VectorStatusTypeEnum;
import com.example.web.mapper.*;
import com.example.web.service.StatisticsService;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private ChatSessionMapper chatSessionMapper;
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private QaFeedbackMapper qaFeedbackMapper;
    @Autowired
    private HotQuestionMapper hotQuestionMapper;
    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;
    @Autowired
    private KnowledgeTypeMapper knowledgeTypeMapper;
    @Autowired
    private DocumentChunkMapper documentChunkMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private AssessmentRecordMapper assessmentRecordMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    // ==================== 用户统计 ====================
    @SneakyThrows
    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 1. 总用户数
        Long totalUsers = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery());
        result.put("totalUsers", totalUsers);

        // 2. 普通用户数 (roleType=2)
        Long normalUsers = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getRoleType, 2));
        result.put("normalUsers", normalUsers);

        // 3. 管理员数 (roleType=1)
        Long adminUsers = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getRoleType, 1));
        result.put("adminUsers", adminUsers);

        // 4. 近30天活跃用户数，按最近消息时间去重统计用户。
        List<ChatSession> sessions = chatSessionMapper.selectList(Wrappers.<ChatSession>lambdaQuery());
        LocalDate today = LocalDate.now();
        LocalDateTime activeStart = today.minusDays(29).atStartOfDay();
        LocalDateTime activeEnd = today.plusDays(1).atStartOfDay();
        long activeUsers = sessions.stream()
                .filter(s -> s.getLastMessageTime() != null && !s.getLastMessageTime().isBefore(activeStart)
                        && s.getLastMessageTime().isBefore(activeEnd))
                .map(ChatSession::getUserId).filter(Objects::nonNull).distinct().count();
        result.put("activeUsers", activeUsers);

        // 5. 近7天新增用户趋势
        List<Map<String, Object>> userTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            Long count = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                    .ge(SysUser::getCreationTime, start).le(SysUser::getCreationTime, end));
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", count);
            userTrend.add(item);
        }
        result.put("userTrend", userTrend);

        // 6. 用户参与度统计
        Long totalSessions = chatSessionMapper.selectCount(Wrappers.<ChatSession>lambdaQuery());
        Long totalAssessments = assessmentRecordMapper.selectCount(Wrappers.<AssessmentRecord>lambdaQuery());
        Long totalFavorites = favoriteMapper.selectCount(Wrappers.<Favorite>lambdaQuery());
        Long totalComments = articleCommentMapper.selectCount(Wrappers.<ArticleComment>lambdaQuery());
        result.put("totalSessions", totalSessions);
        result.put("totalAssessments", totalAssessments);
        result.put("totalFavorites", totalFavorites);
        result.put("totalComments", totalComments);

        // 7. 用户行为分布（饼图）
        List<Map<String, Object>> behaviorDist = new ArrayList<>();
        behaviorDist.add(Map.of("name", "智能问答", "value", totalSessions));
        behaviorDist.add(Map.of("name", "心理测评", "value", totalAssessments));
        behaviorDist.add(Map.of("name", "收藏内容", "value", totalFavorites));
        behaviorDist.add(Map.of("name", "发表评论", "value", totalComments));
        result.put("behaviorDist", behaviorDist);

        // 8. 近7天用户活跃趋势（按当天实际发送消息的用户去重）。
        Map<Integer, Integer> sessionUsers = sessions.stream()
                .filter(s -> s.getUserId() != null)
                .collect(Collectors.toMap(ChatSession::getId, ChatSession::getUserId));
        List<Map<String, Object>> activeTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            long count = chatMessageMapper.selectList(Wrappers.<ChatMessage>lambdaQuery()
                    .eq(ChatMessage::getSenderRole, 1)
                    .ge(ChatMessage::getCreationTime, start).le(ChatMessage::getCreationTime, end))
                    .stream().map(m -> sessionUsers.get(m.getChatSessionId()))
                    .filter(Objects::nonNull).distinct().count();
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", count);
            activeTrend.add(item);
        }
        result.put("activeTrend", activeTrend);

        return result;
    }

    // ==================== 问答统计 ====================
    @SneakyThrows
    @Override
    public Map<String, Object> getQaStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 1. 总对话数
        Long totalSessions = chatSessionMapper.selectCount(Wrappers.<ChatSession>lambdaQuery());
        result.put("totalSessions", totalSessions);

        // 2. 总消息数
        Long totalMessages = chatMessageMapper.selectCount(Wrappers.<ChatMessage>lambdaQuery());
        result.put("totalMessages", totalMessages);

        // 3. 用户消息数
        Long userMessages = chatMessageMapper
                .selectCount(Wrappers.<ChatMessage>lambdaQuery().eq(ChatMessage::getSenderRole, 1));
        result.put("userMessages", userMessages);

        // 4. AI回复数
        Long aiMessages = chatMessageMapper
                .selectCount(Wrappers.<ChatMessage>lambdaQuery().eq(ChatMessage::getSenderRole, 2));
        result.put("aiMessages", aiMessages);

        // 5. 反馈总数
        Long totalFeedbacks = qaFeedbackMapper.selectCount(Wrappers.<QaFeedback>lambdaQuery());
        result.put("totalFeedbacks", totalFeedbacks);

        // 6. 满意数（rating >= 4）
        Long satisfiedCount = qaFeedbackMapper
                .selectCount(Wrappers.<QaFeedback>lambdaQuery().ge(QaFeedback::getRating, 4));
        result.put("satisfiedCount", satisfiedCount);

        // 满意率
        double satisfactionRate = totalFeedbacks > 0 ? Math.round(satisfiedCount * 100.0 / totalFeedbacks * 10) / 10.0
                : 0;
        result.put("satisfactionRate", satisfactionRate);

        // 7. 近7天问答量趋势
        List<Map<String, Object>> qaTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            Long msgCount = chatMessageMapper.selectCount(Wrappers.<ChatMessage>lambdaQuery()
                    .eq(ChatMessage::getSenderRole, 1)
                    .ge(ChatMessage::getCreationTime, start).le(ChatMessage::getCreationTime, end));
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", msgCount);
            qaTrend.add(item);
        }
        result.put("qaTrend", qaTrend);

        // 8. 热门问题TOP10
        List<HotQuestion> hotList = hotQuestionMapper.selectList(
                Wrappers.<HotQuestion>lambdaQuery().orderByDesc(HotQuestion::getAskCount).last("LIMIT 10"));
        List<Map<String, Object>> hotQuestions = hotList.stream().map(h -> {
            Map<String, Object> m = new HashMap<>();
            m.put("question", h.getQuestionText());
            m.put("count", h.getAskCount());
            return m;
        }).collect(Collectors.toList());
        result.put("hotQuestions", hotQuestions);

        // 9. 反馈评分分布
        List<Map<String, Object>> ratingDist = new ArrayList<>();
        for (int r = 1; r <= 5; r++) {
            Long count = qaFeedbackMapper.selectCount(Wrappers.<QaFeedback>lambdaQuery().eq(QaFeedback::getRating, r));
            Map<String, Object> item = new HashMap<>();
            item.put("rating", r);
            item.put("count", count);
            ratingDist.add(item);
        }
        result.put("ratingDist", ratingDist);

        // 10. 消息角色分布（饼图）
        List<Map<String, Object>> roleDist = new ArrayList<>();
        roleDist.add(Map.of("name", "用户提问", "value", userMessages));
        roleDist.add(Map.of("name", "AI回复", "value", aiMessages));
        result.put("roleDist", roleDist);

        return result;
    }

    // ==================== 知识库统计 ====================
    @SneakyThrows
    @Override
    public Map<String, Object> getKnowledgeStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 1. 知识文档总数
        Long totalDocs = knowledgeDocumentMapper.selectCount(Wrappers.<KnowledgeDocument>lambdaQuery());
        result.put("totalDocs", totalDocs);

        // 2. 分块总数
        Long totalChunks = documentChunkMapper.selectCount(Wrappers.<DocumentChunk>lambdaQuery());
        result.put("totalChunks", totalChunks);

        // 3. 知识分类数
        Long totalTypes = knowledgeTypeMapper.selectCount(Wrappers.<KnowledgeType>lambdaQuery());
        result.put("totalTypes", totalTypes);

        // 4. 已完成向量化文档数
        Long vectorizedDocs = knowledgeDocumentMapper.selectCount(Wrappers.<KnowledgeDocument>lambdaQuery()
                .eq(KnowledgeDocument::getVectorStatusType, VectorStatusTypeEnum.已完成.index()));
        result.put("vectorizedDocs", vectorizedDocs);

        // 5. 向量化状态分布（饼图）
        List<Map<String, Object>> vectorStatusDist = new ArrayList<>();
        for (VectorStatusTypeEnum e : VectorStatusTypeEnum.values()) {
            Long count = knowledgeDocumentMapper.selectCount(Wrappers.<KnowledgeDocument>lambdaQuery()
                    .eq(KnowledgeDocument::getVectorStatusType, e.index()));
            vectorStatusDist.add(Map.of("name", e.name(), "value", count));
        }
        result.put("vectorStatusDist", vectorStatusDist);

        // 6. 各分类文档数量（柱状图）
        List<KnowledgeType> types = knowledgeTypeMapper.selectList(Wrappers.<KnowledgeType>lambdaQuery());
        List<Map<String, Object>> typeDist = types.stream().map(t -> {
            Long count = knowledgeDocumentMapper.selectCount(Wrappers.<KnowledgeDocument>lambdaQuery()
                    .eq(KnowledgeDocument::getKnowledgeTypeId, t.getId()));
            Map<String, Object> m = new HashMap<>();
            m.put("name", t.getTypeName());
            m.put("count", count);
            return m;
        }).collect(Collectors.toList());
        result.put("typeDist", typeDist);

        // 7. 各分类分块数量
        List<Map<String, Object>> typeChunkDist = types.stream().map(t -> {
            List<KnowledgeDocument> docs = knowledgeDocumentMapper.selectList(
                    Wrappers.<KnowledgeDocument>lambdaQuery().eq(KnowledgeDocument::getKnowledgeTypeId, t.getId()));
            List<Integer> docIds = docs.stream().map(KnowledgeDocument::getId).collect(Collectors.toList());
            long chunkCount = docIds.isEmpty() ? 0 : documentChunkMapper.selectCount(
                    Wrappers.<DocumentChunk>lambdaQuery().in(DocumentChunk::getKnowledgeDocumentId, docIds));
            Map<String, Object> m = new HashMap<>();
            m.put("name", t.getTypeName());
            m.put("count", chunkCount);
            return m;
        }).collect(Collectors.toList());
        result.put("typeChunkDist", typeChunkDist);

        // 8. 科普文章统计
        Long totalArticles = articleMapper.selectCount(Wrappers.<Article>lambdaQuery());
        result.put("totalArticles", totalArticles);

        return result;
    }

    // ==================== 问答词云 ====================
    @SneakyThrows
    @Override
    public List<Map<String, Object>> getQaWordCloud() {
        // 获取所有用户提问
        List<ChatMessage> userMsgs = chatMessageMapper.selectList(
                Wrappers.<ChatMessage>lambdaQuery().eq(ChatMessage::getSenderRole, 1));
        // 合并所有问题文本
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : userMsgs) {
            if (msg.getMessageContent() != null) {
                sb.append(msg.getMessageContent()).append(" ");
            }
        }
        // 也加入热门问题
        List<HotQuestion> hotList = hotQuestionMapper.selectList(Wrappers.<HotQuestion>lambdaQuery());
        for (HotQuestion h : hotList) {
            if (h.getQuestionText() != null) {
                for (int i = 0; i < (h.getAskCount() != null ? h.getAskCount() : 1); i++) {
                    sb.append(h.getQuestionText()).append(" ");
                }
            }
        }
        return segmentAndCount(sb.toString());
    }

    // ==================== 知识库词云 ====================
    @SneakyThrows
    @Override
    public List<Map<String, Object>> getKnowledgeWordCloud() {
        // 获取所有文档分块内容
        List<DocumentChunk> chunks = documentChunkMapper.selectList(
                Wrappers.<DocumentChunk>lambdaQuery().last("LIMIT 200"));
        StringBuilder sb = new StringBuilder();
        for (DocumentChunk chunk : chunks) {
            if (chunk.getChunkContent() != null) {
                sb.append(chunk.getChunkContent()).append(" ");
            }
        }
        return segmentAndCount(sb.toString());
    }

    /**
     * HanLP分词并统计词频，返回词云数据
     */
    private List<Map<String, Object>> segmentAndCount(String text) {
        if (text == null || text.trim().isEmpty())
            return List.of();

        // 使用HanLP分词
        List<Term> terms = HanLP.segment(text);

        // 统计词频（过滤停用词和单字）
        Map<String, Integer> wordCount = new HashMap<>();
        Set<String> stopWords = Set.of("的", "了", "是", "在", "我", "有", "和", "就", "不", "人", "都", "一", "一个",
                "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好", "自己", "这", "他",
                "她", "它", "们", "那", "被", "从", "把", "让", "用", "对", "为", "与", "等", "能", "可以", "吗",
                "呢", "吧", "啊", "哦", "嗯", "呀", "么", "什么", "怎么", "怎么办", "如何", "可能", "因为", "所以",
                "但是", "而且", "或者", "如果", "虽然", "还是", "已经", "正在", "应该", "需要", "比较", "非常",
                "特别", "一些", "这些", "那些", "其他", "之", "以", "及", "而", "或", "但", "且", "则");

        for (Term term : terms) {
            String word = term.word.trim();
            // 过滤：长度>=2，非停用词，非纯数字/标点
            if (word.length() >= 2 && !stopWords.contains(word) && word.matches(".*[\\u4e00-\\u9fa5a-zA-Z].*")) {
                wordCount.merge(word, 1, Integer::sum);
            }
        }

        // 按词频排序取TOP80
        return wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(80)
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", e.getKey());
                    m.put("value", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }
}
