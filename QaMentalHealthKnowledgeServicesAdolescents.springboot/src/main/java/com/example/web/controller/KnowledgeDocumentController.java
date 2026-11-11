package com.example.web.controller;

import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.mapper.*;
import com.example.web.service.*;
import com.example.web.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.SneakyThrows;

/**
 * 知识文档控制器
 */
@RestController()
@RequestMapping("/knowledgeDocument")
public class KnowledgeDocumentController {
    @Autowired
    private com.example.web.service.VectorService vectorService;
    @Autowired private com.example.web.service.VectorIndexAudit vectorIndexAudit;
    @Autowired private DocumentSources documentSources;
    @Autowired private KnowledgeDocumentMapper knowledgeDocumentMapper;

    @PostMapping("/sources")
    public java.util.List<DocumentSources.Reference> sources(@RequestBody IdInput input) {
        var doc = input.getId() == null ? null : knowledgeDocumentMapper.selectById(input.getId());
        return doc == null ? java.util.List.of() : documentSources.read(doc.getFileUrl());
    }

    @RequestMapping(value = "/indexAudit", method = RequestMethod.POST)
    public java.util.Map<String,Object> indexAudit() { return vectorIndexAudit.inspect(); }

    @RequestMapping(value = "/rebuildAllVectors", method = RequestMethod.POST)
    public void rebuildAllVectors() { vectorService.rebuildAllVectors(); }

    @RequestMapping(value = "/rebuildVectors", method = RequestMethod.POST)
    public void rebuildVectors(@RequestBody IdInput input) {
        if (input.getId() == null) throw new com.example.web.exception.CustomException("请选择文档");
        vectorService.rebuildVectors(input.getId());
    }

    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;

    /**
     * 知识文档分页查询
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<KnowledgeDocumentDto> list(@RequestBody KnowledgeDocumentPagedInput input) {
        return knowledgeDocumentService.list(input);
    }

    /**
     * 单个知识文档查询接口
     */
    @RequestMapping(value = "/findById", method = RequestMethod.POST)
    @SneakyThrows
    public KnowledgeDocumentDto findById(@RequestBody KnowledgeDocumentPagedInput input) {
        return knowledgeDocumentService.findById(input);
    }

    /**
     * 知识文档创建或修改
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public KnowledgeDocumentDto addOrEdit(@RequestBody KnowledgeDocumentDto input) throws Exception {
        return knowledgeDocumentService.addOrEdit(input);
    }

    /**
     * 上传知识文档并触发向量化
     */
    @RequestMapping(value = "/uploadDocument", method = RequestMethod.POST)
    public KnowledgeDocumentDto uploadDocument(@RequestBody KnowledgeDocumentDto input) throws Exception {
        return knowledgeDocumentService.uploadDocument(input);
    }

    /**
     * 重新向量化
     */
    @RequestMapping(value = "/reVectorize", method = RequestMethod.POST)
    public void reVectorize(@RequestBody IdInput input) {
        knowledgeDocumentService.reVectorize(input);
    }

    /**
     * 知识文档删除
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public void deleteById(@RequestBody IdInput input) {
        knowledgeDocumentService.deleteById(input);
    }

    /**
     * 知识文档批量删除
     */
    @RequestMapping(value = "/batchDeleteByIds", method = RequestMethod.POST)
    public void batchDeleteByIds(@RequestBody IdsInput input) {
        knowledgeDocumentService.batchDeleteByIds(input);
    }

    /**
     * AI汇总文档内容
     */
    @RequestMapping(value = "/aiSummary", method = RequestMethod.POST)
    public String aiSummary(@RequestBody IdInput input) {
        return knowledgeDocumentService.aiSummary(input);
    }

    /**
     * 解析上传文件，AI自动提取标题、分类、描述
     */
    @RequestMapping(value = "/parseFile", method = RequestMethod.POST)
    public java.util.Map<String, Object> parseFile(@RequestBody java.util.Map<String, String> input) {
        return knowledgeDocumentService.parseFile(input.get("fileUrl"));
    }
}
