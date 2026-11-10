package com.example.web.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
 * 文档分块功能实现类
 */
@Service
public class DocumentChunkServiceImpl extends ServiceImpl<DocumentChunkMapper, DocumentChunk> implements DocumentChunkService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的DocumentChunk表mapper对象
     */
    @Autowired
    private DocumentChunkMapper documentChunkMapper;
    @Autowired
    private KnowledgeDocumentMapper  knowledgeDocumentMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<DocumentChunk> builderQuery(DocumentChunkPagedInput input) {
       //声明一个支持文档分块查询的(拉姆达)表达式
        LambdaQueryWrapper<DocumentChunk> queryWrapper = Wrappers.<DocumentChunk>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, DocumentChunk::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getVectorId())) {
             queryWrapper = queryWrapper.like(DocumentChunk::getVectorId, input.getVectorId());
       	 }

        if (input.getKnowledgeDocumentId() != null) {
            queryWrapper = queryWrapper.eq(DocumentChunk::getKnowledgeDocumentId, input.getKnowledgeDocumentId());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(DocumentChunk::getVectorId,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理文档分块对于的外键数据
     */
   private List<DocumentChunkDto> dispatchItem(List<DocumentChunkDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (DocumentChunkDto item : items) {           
          	            
           //查询出关联的KnowledgeDocument表信息           
            KnowledgeDocument  knowledgeDocument= knowledgeDocumentMapper.selectById(item.getKnowledgeDocumentId());
            item.setKnowledgeDocumentDto(knowledgeDocument!=null?knowledgeDocument.mapToDto():new KnowledgeDocumentDto());              
       }
       
     return items; 
   }
  
    /**
     * 文档分块分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<DocumentChunkDto> list(DocumentChunkPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<DocumentChunk> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(DocumentChunk::getCreationTime);
        }

        //构建一个分页查询的model
        Page<DocumentChunk> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取文档分块数据
        IPage<DocumentChunk> pageRecords= documentChunkMapper.selectPage(page, queryWrapper);
      
        //把DocumentChunk实体转换成DocumentChunk传输模型
        List<DocumentChunkDto> items= Extension.copyBeanList(pageRecords.getRecords(),DocumentChunkDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个文档分块查询
     */
    @SneakyThrows
    @Override
    public DocumentChunkDto findById(DocumentChunkPagedInput input) {
       if(input.getId()==null)
        {
         return new DocumentChunkDto();
        }
      
       PagedResult<DocumentChunkDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new DocumentChunkDto()); 
    }

    /**
     *文档分块创建或者修改
     */
    @SneakyThrows
    @Override
    public DocumentChunkDto addOrEdit(DocumentChunkDto input) {
        //声明一个文档分块实体
        DocumentChunk documentChunk=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(documentChunk);
        //把传输模型返回给前端
        return documentChunk.mapToDto();
    }
    /**
     * 文档分块删除
     */
    @Override
    public void deleteById(IdInput input) {
        DocumentChunk entity = documentChunkMapper.selectById(input.getId());
        documentChunkMapper.deleteById(entity);
    }

    /**
     * 文档分块批量删除
     */
    @Override
    public void batchDeleteByIds(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            deleteById(idInput);
        }
    }
}
