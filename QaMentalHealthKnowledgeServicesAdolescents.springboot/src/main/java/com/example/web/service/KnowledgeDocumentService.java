package com.example.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.common.*;
import com.example.web.enums.*;
import java.lang.reflect.InvocationTargetException;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 知识文档功能的Service接口的定义清单
 */
public interface KnowledgeDocumentService extends IService<KnowledgeDocument> {

    /**
     * 知识文档的分页查询方法接口定义
     */
    public PagedResult<KnowledgeDocumentDto> list(KnowledgeDocumentPagedInput input);

    /**
     * 知识文档的新增或者修改方法接口定义
     */
    public KnowledgeDocumentDto addOrEdit(KnowledgeDocumentDto input);

    /**
     * 获取知识文档信息
     */
    public KnowledgeDocumentDto findById(KnowledgeDocumentPagedInput input);

    /**
     * 知识文档删除
     */
    public void deleteById(IdInput input);

    /**
     * 知识文档批量删除
     */
    public void batchDeleteByIds(IdsInput input);

    /**
     * 上传知识文档并触发向量化
     */
    public KnowledgeDocumentDto uploadDocument(KnowledgeDocumentDto input);

    /**
     * 重新向量化
     */
    public void reVectorize(IdInput input);

    /**
     * AI汇总文档内容
     */
    public String aiSummary(IdInput input);

    /**
     * 解析上传文件，AI自动提取标题、分类、描述
     */
    public java.util.Map<String, Object> parseFile(String fileUrl);

}
