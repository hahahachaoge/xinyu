package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.config.DeepSeekConfig;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.SysConst;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.enums.*;
import com.example.web.service.*;
import com.example.web.common.*;
import com.example.web.exception.CustomException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import lombok.SneakyThrows;
import java.io.IOException;
import com.example.web.utils.*;
import java.text.DecimalFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 知识文档功能实现类
 */
@Service
public class KnowledgeDocumentServiceImpl extends ServiceImpl<KnowledgeDocumentMapper, KnowledgeDocument>
        implements KnowledgeDocumentService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的KnowledgeDocument表mapper对象
     */
    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;
    @Autowired
    private KnowledgeTypeMapper knowledgeTypeMapper;
    @Autowired
    private DocumentChunkMapper documentChunkMapper;
    @Autowired
    private VectorService vectorService;

    @Autowired
    private DeepSeekConfig deepSeekConfig;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<KnowledgeDocument> builderQuery(KnowledgeDocumentPagedInput input) {
        // 声明一个支持知识文档查询的(拉姆达)表达式
        LambdaQueryWrapper<KnowledgeDocument> queryWrapper = Wrappers.<KnowledgeDocument>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, KnowledgeDocument::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getDocTitle())) {
            queryWrapper = queryWrapper.like(KnowledgeDocument::getDocTitle, input.getDocTitle());
        }
        if (Extension.isNotNullOrEmpty(input.getFileFormat())) {
            queryWrapper = queryWrapper.like(KnowledgeDocument::getFileFormat, input.getFileFormat());
        }
        if (Extension.isNotNullOrEmpty(input.getFileSize())) {
            queryWrapper = queryWrapper.like(KnowledgeDocument::getFileSize, input.getFileSize());
        }

        if (input.getKnowledgeTypeId() != null) {
            queryWrapper = queryWrapper.eq(KnowledgeDocument::getKnowledgeTypeId, input.getKnowledgeTypeId());
        }

        if (input.getVectorStatusType() != null) {
            queryWrapper = queryWrapper.eq(KnowledgeDocument::getVectorStatusType, input.getVectorStatusType());
        }

        if (input.getUploaderId() != null) {
            queryWrapper = queryWrapper.eq(KnowledgeDocument::getUploaderId, input.getUploaderId());
        }
        if (input.getVectorTimeRange() != null && !input.getVectorTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(KnowledgeDocument::getVectorTime, input.getVectorTimeRange().get(1));
            queryWrapper = queryWrapper.ge(KnowledgeDocument::getVectorTime, input.getVectorTimeRange().get(0));
        }

        if (Extension.isNotNullOrEmpty(input.getKeyWord())) {
            queryWrapper = queryWrapper.and(i -> i
                    .like(KnowledgeDocument::getDocTitle, input.getKeyWord()).or()
                    .like(KnowledgeDocument::getFileFormat, input.getKeyWord()).or()
                    .like(KnowledgeDocument::getFileSize, input.getKeyWord()).or());

        }

        return queryWrapper;
    }

    /**
     * 处理知识文档对于的外键数据
     */
    private List<KnowledgeDocumentDto> dispatchItem(List<KnowledgeDocumentDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (KnowledgeDocumentDto item : items) {

            // 查询出关联的SysUser表信息
            SysUser uploader = sysUserMapper.selectById(item.getUploaderId());
            item.setUploaderDto(uploader != null ? uploader.mapToDto() : new SysUserDto());

            // 查询出关联的KnowledgeType表信息
            KnowledgeType knowledgeType = knowledgeTypeMapper.selectById(item.getKnowledgeTypeId());
            item.setKnowledgeTypeDto(knowledgeType != null ? knowledgeType.mapToDto() : new KnowledgeTypeDto());
        }

        return items;
    }

    /**
     * 知识文档分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<KnowledgeDocumentDto> list(KnowledgeDocumentPagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<KnowledgeDocument> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(KnowledgeDocument::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<KnowledgeDocument> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取知识文档数据
        IPage<KnowledgeDocument> pageRecords = knowledgeDocumentMapper.selectPage(page, queryWrapper);

        // 把KnowledgeDocument实体转换成KnowledgeDocument传输模型
        List<KnowledgeDocumentDto> items = Extension.copyBeanList(pageRecords.getRecords(), KnowledgeDocumentDto.class);

        dispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }

    /**
     * 单个知识文档查询
     */
    @SneakyThrows
    @Override
    public KnowledgeDocumentDto findById(KnowledgeDocumentPagedInput input) {
        if (input.getId() == null) {
            return new KnowledgeDocumentDto();
        }

        PagedResult<KnowledgeDocumentDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new KnowledgeDocumentDto());
    }

    /**
     * 知识文档创建或者修改
     */
    @SneakyThrows
    @Override
    public KnowledgeDocumentDto addOrEdit(KnowledgeDocumentDto input) {
        // 声明一个知识文档实体
        KnowledgeDocument knowledgeDocument = input.mapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(knowledgeDocument);
        // 把传输模型返回给前端
        return knowledgeDocument.mapToDto();
    }

    /**
     * 知识文档删除（同时删除关联的分块数据）
     */
    @Override
    public void deleteById(IdInput input) {
        var deleting = knowledgeDocumentMapper.selectById(input.getId());
        if (deleting != null && Integer.valueOf(2).equals(deleting.getVectorStatusType()))
            throw new CustomException("文档正在处理中，请稍后删除");
        vectorService.removeVectors(documentChunkMapper.selectList(Wrappers.<DocumentChunk>lambdaQuery()
            .eq(DocumentChunk::getKnowledgeDocumentId,input.getId())).stream().map(DocumentChunk::getVectorId).toList());
        // 先删除关联的文档分块
        documentChunkMapper.delete(Wrappers.<DocumentChunk>lambdaQuery()
                .eq(DocumentChunk::getKnowledgeDocumentId, input.getId()));
        // 再删除文档本身
        KnowledgeDocument entity = knowledgeDocumentMapper.selectById(input.getId());
        if (entity != null) {
            knowledgeDocumentMapper.deleteById(entity);
        }
    }

    /**
     * 知识文档批量删除
     */
    @Override
    public void batchDeleteByIds(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            deleteById(idInput);
        }
    }

    /**
     * 上传知识文档并触发向量化
     */
    @SneakyThrows
    @Override
    public KnowledgeDocumentDto uploadDocument(KnowledgeDocumentDto input) {
        KnowledgeDocument entity = input.mapToEntity();
        // 设置初始向量化状态为待处理
        entity.setVectorStatusType(VectorStatusTypeEnum.待处理.index());
        entity.setChunkCount(0);
        entity.setCreationTime(java.time.LocalDateTime.now());
        // 根据文件URL自动识别文件格式和大小
        if (Extension.isNotNullOrEmpty(entity.getFileUrl())) {
            String url = entity.getFileUrl();
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase() : "";
            entity.setFileFormat(ext);

            // 计算文件大小
            try {
                String relativePath = url.replaceFirst("http://[^/]+/", "");
                String localPath = System.getProperty("user.dir") + "/external-resources/" + relativePath;
                java.io.File file = new java.io.File(localPath);
                if (file.exists()) {
                    long bytes = file.length();
                    if (bytes < 1024) {
                        entity.setFileSize(bytes + "B");
                    } else if (bytes < 1024 * 1024) {
                        entity.setFileSize(String.format("%.1fKB", bytes / 1024.0));
                    } else {
                        entity.setFileSize(String.format("%.1fMB", bytes / (1024.0 * 1024)));
                    }
                }
            } catch (Exception ignored) {
            }
        }
        saveOrUpdate(entity);

        // 异步触发向量化
        vectorService.vectorizeDocument(entity.getId());

        return entity.mapToDto();
    }

    /**
     * 重新向量化
     */
    @SneakyThrows
    @Override
    public void reVectorize(IdInput input) {
        KnowledgeDocument doc = knowledgeDocumentMapper.selectById(input.getId());
        if (doc == null) {
            throw new CustomException("知识文档不存在");
        }
        if (doc.getVectorStatusType() != null && doc.getVectorStatusType() == VectorStatusTypeEnum.处理中.index()) {
            throw new CustomException("文档正在处理中，请稍后再试");
        }
        if (Extension.isNullOrEmpty(doc.getFileUrl())) {
            throw new CustomException("文件路径为空，无法向量化");
        }
        // 异步触发向量化
        vectorService.vectorizeDocument(doc.getId());
    }

    /**
     * AI汇总文档内容
     * 1. 根据fileUrl解析本地文件路径
     * 2. 使用Apache Tika解析文件内容（支持PDF/TXT/MD）
     * 3. 调用DeepSeek API生成摘要
     * 4. 保存到docDesc字段
     */
    @SneakyThrows
    @Override
    public String aiSummary(IdInput input) {
        KnowledgeDocument doc = knowledgeDocumentMapper.selectById(input.getId());
        if (doc == null) {
            throw new CustomException("知识文档不存在");
        }
        if (Extension.isNullOrEmpty(doc.getFileUrl())) {
            throw new CustomException("文件路径为空，无法解析");
        }

        // 1. 解析文件内容
        String fileContent = parseFileContent(doc.getFileUrl());
        if (Extension.isNullOrEmpty(fileContent)) {
            throw new CustomException("文件内容为空，无法汇总");
        }

        // 截取前3000字符避免token超限
        if (fileContent.length() > 3000) {
            fileContent = fileContent.substring(0, 3000);
        }

        // 2. 调用DeepSeek API生成摘要
        String summary = callDeepSeekSummary(fileContent, doc.getDocTitle());

        // 3. 保存到docDesc
        doc.setDocDesc(summary);
        knowledgeDocumentMapper.updateById(doc);

        return summary;
    }

    /**
     * 解析文件内容（使用Apache Tika）
     */
    private String parseFileContent(String fileUrl) {
        try {
            java.io.InputStream inputStream;
            if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
                // 从URL路径解析出本地文件路径
                String relativePath = fileUrl.replaceFirst("http://[^/]+/", "");
                String localPath = System.getProperty("user.dir") + "/external-resources/" + relativePath;
                java.io.File file = new java.io.File(localPath);
                if (file.exists()) {
                    inputStream = new java.io.FileInputStream(file);
                } else {
                    // 尝试直接从URL下载
                    inputStream = new java.net.URL(fileUrl).openStream();
                }
            } else {
                inputStream = new java.io.FileInputStream(new java.io.File(fileUrl));
            }

            dev.langchain4j.data.document.DocumentParser parser = new dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser();
            dev.langchain4j.data.document.Document document = parser.parse(inputStream);
            return document.text();
        } catch (Exception e) {
            throw new RuntimeException("文件解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 调用DeepSeek API生成文档摘要
     */
    private String callDeepSeekSummary(String content, String title) {
        try {
            String prompt = "请对以下知识文档内容进行专业的摘要汇总，要求：\n" +
                    "1. 用简洁的语言概括文档的核心内容和要点\n" +
                    "2. 摘要控制在200字以内\n" +
                    "3. 突出文档的主题和关键信息\n" +
                    "4. 使用中文回答\n\n" +
                    "文档标题：" + (title != null ? title : "") + "\n\n" +
                    "文档内容：\n" + content;

            String escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");

            String requestBody = "{\"model\":" + new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(deepSeekConfig.getModelName()) + ",\"stream\":false,\"temperature\":0.5,\"max_tokens\":500,"
                    +
                    "\"messages\":[{\"role\":\"system\",\"content\":\"你是一个专业的文档摘要助手，擅长提取文档核心内容并生成简洁的摘要。\"}," +
                    "{\"role\":\"user\",\"content\":\"" + escapedPrompt + "\"}]}";

            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(deepSeekConfig.getCompletionsUrl()).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + deepSeekConfig.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(120000);

            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }

            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(sb.toString());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            return "AI汇总失败：" + e.getMessage();
        }
    }

    /**
     * 解析上传文件，AI自动提取标题、分类建议、描述
     */
    @SneakyThrows
    @Override
    public java.util.Map<String, Object> parseFile(String fileUrl) {
        if (Extension.isNullOrEmpty(fileUrl)) {
            throw new CustomException("文件路径为空");
        }

        // 1. 解析文件内容
        String fileContent = parseFileContent(fileUrl);
        if (Extension.isNullOrEmpty(fileContent)) {
            throw new CustomException("文件内容为空");
        }

        // 从文件名提取默认标题
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        // URL解码文件名
        try {
            fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
        } catch (Exception ignored) {
        }

        // 截取前2000字符
        String contentForAi = fileContent.length() > 2000 ? fileContent.substring(0, 2000) : fileContent;

        // 2. 获取知识分类列表
        List<KnowledgeType> types = knowledgeTypeMapper
                .selectList(com.baomidou.mybatisplus.core.toolkit.Wrappers.<KnowledgeType>lambdaQuery());
        StringBuilder typeList = new StringBuilder();
        for (KnowledgeType t : types) {
            typeList.append(t.getId()).append(":").append(t.getTypeName()).append(", ");
        }

        // 3. 调用DeepSeek AI提取信息
        String prompt = "请分析以下文档内容，返回JSON格式（不要返回其他内容，只返回纯JSON）：\n" +
                "{\"title\":\"文档标题\",\"categoryId\":分类ID数字,\"description\":\"200字以内的文档描述\"}\n\n" +
                "可选的知识分类（ID:名称）：" + typeList + "\n" +
                "请根据内容选择最匹配的分类ID。如果都不匹配，返回null。\n\n" +
                "文件名：" + fileName + "\n\n" +
                "文档内容：\n" + contentForAi;

        String aiResult = callDeepSeekForParse(prompt);

        // 4. 解析AI返回的JSON
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("title", fileName); // 默认用文件名
        result.put("categoryId", null);
        result.put("description", "");

        try {
            // 提取JSON部分（AI可能返回带markdown代码块的格式）
            String jsonStr = aiResult;
            if (jsonStr.contains("```")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("{"), jsonStr.lastIndexOf("}") + 1);
            }
            jsonStr = jsonStr.trim();
            if (jsonStr.startsWith("{")) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(jsonStr);
                if (node.has("title") && !node.get("title").isNull()) {
                    result.put("title", node.get("title").asText());
                }
                if (node.has("categoryId") && !node.get("categoryId").isNull()) {
                    result.put("categoryId", node.get("categoryId").asInt());
                }
                if (node.has("description") && !node.get("description").isNull()) {
                    result.put("description", node.get("description").asText());
                }
            }
        } catch (Exception e) {
            // 解析失败用默认值
        }

        return result;
    }

    /**
     * 调用DeepSeek API解析文件信息
     */
    private String callDeepSeekForParse(String prompt) {
        try {
            String escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");

            String requestBody = "{\"model\":" + new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(deepSeekConfig.getModelName()) + ",\"stream\":false,\"temperature\":0.3,\"max_tokens\":500,"
                    +
                    "\"messages\":[{\"role\":\"system\",\"content\":\"你是一个文档分析助手，只返回纯JSON格式，不要返回其他内容。\"}," +
                    "{\"role\":\"user\",\"content\":\"" + escapedPrompt + "\"}]}";

            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(deepSeekConfig.getCompletionsUrl()).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + deepSeekConfig.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(120000);

            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }

            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(sb.toString());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "{}";
        }
    }
}
