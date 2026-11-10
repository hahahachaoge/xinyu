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
 * 文档分块功能的Service接口的定义清单
 */
public interface DocumentChunkService extends IService<DocumentChunk> {

    /**
     * 文档分块的分页查询方法接口定义
     */
    public PagedResult<DocumentChunkDto> list(DocumentChunkPagedInput input) ;
    /**
     * 文档分块的新增或者修改方法接口定义
     */
    public DocumentChunkDto addOrEdit(DocumentChunkDto input);

     /**
     * 获取文档分块信息
     */
    public DocumentChunkDto findById(DocumentChunkPagedInput input);
 	 /**
     * 文档分块删除
     */
    public void deleteById(IdInput input);

    /**
     * 文档分块批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
