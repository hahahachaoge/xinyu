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
 * 科普文章功能的Service接口的定义清单
 */
public interface ArticleService extends IService<Article> {

    /**
     * 科普文章的分页查询方法接口定义
     */
    public PagedResult<ArticleDto> list(ArticlePagedInput input);

    /**
     * 科普文章的新增或者修改方法接口定义
     */
    public ArticleDto addOrEdit(ArticleDto input);

    /**
     * 获取科普文章信息
     */
    public ArticleDto findById(ArticlePagedInput input);

    /**
     * 科普文章删除
     */
    public void deleteById(IdInput input);

    /**
     * 科普文章批量删除
     */
    public void batchDeleteByIds(IdsInput input);

    /**
     * 浏览文章（增加浏览量）
     */
    public void view(IdInput input);

    /**
     * 基于协同过滤的文章推荐
     */
    public List<ArticleDto> recommend(Integer userId, Integer currentArticleId);
}
