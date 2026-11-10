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
 * 文章评论功能的Service接口的定义清单
 */
public interface ArticleCommentService extends IService<ArticleComment> {

    /**
     * 文章评论的分页查询方法接口定义
     */
    public PagedResult<ArticleCommentDto> list(ArticleCommentPagedInput input) ;
    /**
     * 文章评论的新增或者修改方法接口定义
     */
    public ArticleCommentDto addOrEdit(ArticleCommentDto input);

     /**
     * 获取文章评论信息
     */
    public ArticleCommentDto findById(ArticleCommentPagedInput input);
 	 /**
     * 文章评论删除
     */
    public void deleteById(IdInput input);

    /**
     * 文章评论批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
