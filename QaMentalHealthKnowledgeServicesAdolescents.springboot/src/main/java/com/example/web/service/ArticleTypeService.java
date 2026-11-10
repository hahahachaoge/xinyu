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
 * 文章分类功能的Service接口的定义清单
 */
public interface ArticleTypeService extends IService<ArticleType> {

    /**
     * 文章分类的分页查询方法接口定义
     */
    public PagedResult<ArticleTypeDto> list(ArticleTypePagedInput input) ;
    /**
     * 文章分类的新增或者修改方法接口定义
     */
    public ArticleTypeDto addOrEdit(ArticleTypeDto input);

     /**
     * 获取文章分类信息
     */
    public ArticleTypeDto findById(ArticleTypePagedInput input);
 	 /**
     * 文章分类删除
     */
    public void deleteById(IdInput input);

    /**
     * 文章分类批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
