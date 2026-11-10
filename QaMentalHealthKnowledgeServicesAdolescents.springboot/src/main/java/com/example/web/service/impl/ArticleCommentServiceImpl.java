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
 * 文章评论功能实现类
 */
@Service
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleComment>
        implements ArticleCommentService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的ArticleComment表mapper对象
     */
    @Autowired
    private ArticleCommentMapper articleCommentMapper;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<ArticleComment> builderQuery(ArticleCommentPagedInput input) {
        // 声明一个支持文章评论查询的(拉姆达)表达式
        LambdaQueryWrapper<ArticleComment> queryWrapper = Wrappers.<ArticleComment>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, ArticleComment::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件

        if (input.getArticleId() != null) {
            queryWrapper = queryWrapper.eq(ArticleComment::getArticleId, input.getArticleId());
        }

        if (input.getUserId() != null) {
            queryWrapper = queryWrapper.eq(ArticleComment::getUserId, input.getUserId());
        }

        return queryWrapper;
    }

    /**
     * 处理文章评论对于的外键数据
     */
    private List<ArticleCommentDto> dispatchItem(List<ArticleCommentDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (ArticleCommentDto item : items) {

            // 查询出关联的Article表信息
            Article article = articleMapper.selectById(item.getArticleId());
            item.setArticleDto(article != null ? article.mapToDto() : new ArticleDto());

            // 查询出关联的SysUser表信息
            SysUser user = sysUserMapper.selectById(item.getUserId());
            item.setUserDto(publicProfile(user));

            // 查询出回复目标用户信息
            if (item.getReplyToUserId() != null) {
                SysUser replyToUser = sysUserMapper.selectById(item.getReplyToUserId());
                item.setReplyToUserDto(publicProfile(replyToUser));
            }
        }

        return items;
    }

    private SysUserDto publicProfile(SysUser user) {
        return user == null ? new SysUserDto() : user.mapToPublicDto();
    }

    /**
     * 文章评论分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<ArticleCommentDto> list(ArticleCommentPagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<ArticleComment> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(ArticleComment::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<ArticleComment> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取文章评论数据
        IPage<ArticleComment> pageRecords = articleCommentMapper.selectPage(page, queryWrapper);

        // 把ArticleComment实体转换成ArticleComment传输模型
        List<ArticleCommentDto> items = Extension.copyBeanList(pageRecords.getRecords(), ArticleCommentDto.class);

        dispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }

    /**
     * 单个文章评论查询
     */
    @SneakyThrows
    @Override
    public ArticleCommentDto findById(ArticleCommentPagedInput input) {
        if (input.getId() == null) {
            return new ArticleCommentDto();
        }

        PagedResult<ArticleCommentDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new ArticleCommentDto());
    }

    /**
     * 文章评论创建或者修改
     */
    @SneakyThrows
    @Override
    public ArticleCommentDto addOrEdit(ArticleCommentDto input) {
        // 声明一个文章评论实体
        Integer userId = AccessControl.requireUser().getUserId();
        ArticleComment articleComment;
        if (!input.isAdd()) {
            articleComment = articleCommentMapper.selectById(input.getId());
            if (articleComment == null) throw new CustomException("评论不存在");
            AccessControl.requireOwner(articleComment.getUserId());
            articleComment.setCommentContent(input.getCommentContent());
        } else {
            articleComment = input.mapToEntity();
            articleComment.setUserId(userId);
            articleComment.setCreationTime(java.time.LocalDateTime.now());
            if (articleMapper.selectById(input.getArticleId()) == null) throw new CustomException("文章不存在");
            if (input.getParentId() != null && input.getParentId() != 0) {
                ArticleComment parent = articleCommentMapper.selectById(input.getParentId());
                if (parent == null || !parent.getArticleId().equals(input.getArticleId()))
                    throw new CustomException("回复的评论不存在或不属于该文章");
                articleComment.setReplyToUserId(parent.getUserId());
            } else articleComment.setReplyToUserId(null);
        }
        // 调用数据库的增加或者修改方法
        saveOrUpdate(articleComment);
        // 把传输模型返回给前端
        return articleComment.mapToDto();
    }

    /**
     * 文章评论删除
     */
    @Override
    public void deleteById(IdInput input) {
        ArticleComment entity = articleCommentMapper.selectById(input.getId());
        if (entity == null) return;
        AccessControl.requireOwner(entity.getUserId());
        articleCommentMapper.deleteById(entity);
    }

    /**
     * 文章评论批量删除
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
