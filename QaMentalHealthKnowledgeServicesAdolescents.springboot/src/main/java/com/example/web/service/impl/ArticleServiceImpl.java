package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
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
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
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
 * 科普文章功能实现类
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的Article表mapper对象
     */
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTypeMapper articleTypeMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<Article> builderQuery(ArticlePagedInput input) {
        // 声明一个支持科普文章查询的(拉姆达)表达式
        LambdaQueryWrapper<Article> queryWrapper = Wrappers.<Article>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, Article::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getTitle())) {
            queryWrapper = queryWrapper.like(Article::getTitle, input.getTitle());
        }

        if (input.getArticleTypeId() != null) {
            queryWrapper = queryWrapper.eq(Article::getArticleTypeId, input.getArticleTypeId());
        }

        if (input.getPublisherId() != null) {
            queryWrapper = queryWrapper.eq(Article::getPublisherId, input.getPublisherId());
        }
        if (input.getPublishTimeRange() != null && !input.getPublishTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(Article::getPublishTime, input.getPublishTimeRange().get(1));
            queryWrapper = queryWrapper.ge(Article::getPublishTime, input.getPublishTimeRange().get(0));
        }

        if (Extension.isNotNullOrEmpty(input.getKeyWord())) {
            queryWrapper = queryWrapper.and(i -> i
                    .like(Article::getTitle, input.getKeyWord()).or());

        }

        return queryWrapper;
    }

    /**
     * 处理科普文章对于的外键数据
     */
    private List<ArticleDto> dispatchItem(List<ArticleDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (ArticleDto item : items) {

            // 查询出关联的SysUser表信息
            SysUser publisher = sysUserMapper.selectById(item.getPublisherId());
            item.setPublisherDto(publisher != null ? publisher.mapToDto() : new SysUserDto());

            // 查询出关联的ArticleType表信息
            ArticleType articleType = articleTypeMapper.selectById(item.getArticleTypeId());
            item.setArticleTypeDto(articleType != null ? articleType.mapToDto() : new ArticleTypeDto());
        }

        return items;
    }

    /**
     * 科普文章分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<ArticleDto> list(ArticlePagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<Article> queryWrapper = builderQuery(input);
        SortItemDto sort = input.getSortItem();
        SFunction<Article, ?> sortColumn = sort == null ? null : switch (
                sort.getFieldName() == null ? "" : sort.getFieldName()) {
            case "id" -> Article::getId;
            case "title" -> Article::getTitle;
            case "viewCount", "view_count" -> Article::getViewCount;
            case "publishTime", "publish_time" -> Article::getPublishTime;
            case "creationTime", "creation_time" -> Article::getCreationTime;
            case "articleTypeId", "article_type_id" -> Article::getArticleTypeId;
            case "publisherId", "publisher_id" -> Article::getPublisherId;
            default -> null;
        };
        if (sortColumn != null) {
            queryWrapper.orderBy(true, Boolean.TRUE.equals(sort.getIsAsc()), sortColumn);
        } else {
            queryWrapper.orderByDesc(Article::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<Article> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取科普文章数据
        IPage<Article> pageRecords = articleMapper.selectPage(page, queryWrapper);

        // 把Article实体转换成Article传输模型
        List<ArticleDto> items = Extension.copyBeanList(pageRecords.getRecords(), ArticleDto.class);

        dispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }

    /**
     * 单个科普文章查询
     */
    @SneakyThrows
    @Override
    public ArticleDto findById(ArticlePagedInput input) {
        if (input.getId() == null) {
            return new ArticleDto();
        }

        PagedResult<ArticleDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new ArticleDto());
    }

    /**
     * 科普文章创建或者修改
     */
    @SneakyThrows
    @Override
    public ArticleDto addOrEdit(ArticleDto input) {
        // 声明一个科普文章实体
        Article article = input.mapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(article);
        // 把传输模型返回给前端
        return article.mapToDto();
    }

    /**
     * 科普文章删除
     */
    @Override
    public void deleteById(IdInput input) {
        Article entity = articleMapper.selectById(input.getId());
        articleMapper.deleteById(entity);
    }

    /**
     * 科普文章批量删除
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
     * 浏览文章（重新统计浏览量+1）
     */
    @SneakyThrows
    @Override
    public void view(IdInput input) {
        Article article = articleMapper.selectById(input.getId());
        if (article != null) {
            article.setViewCount((article.getViewCount() != null ? article.getViewCount() : 0) + 1);
            articleMapper.updateById(article);
        }
    }

    /**
     * 基于协同过滤的文章推荐
     * 算法：基于用户行为（收藏、评论）的协同过滤
     * 1. 找到当前用户收藏/评论过的文章分类
     * 2. 找到与当前用户有相似行为的其他用户（收藏/评论了相同文章的用户）
     * 3. 获取相似用户收藏/评论过但当前用户未看过的文章
     * 4. 按相似用户数量排序，取Top推荐
     * 5. 兜底：同分类热门文章补充
     */
    @SneakyThrows
    @Override
    public List<ArticleDto> recommend(Integer userId, Integer currentArticleId) {
        Set<Integer> recommendIds = new HashSet<>();
        Set<Integer> myArticleIds = new HashSet<>();

        // 1. 获取当前用户收藏过的文章ID
        List<Favorite> myFavorites = favoriteMapper.selectList(
                Wrappers.<Favorite>lambdaQuery()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getFavoriteType, FavoriteTypeEnum.科普文章.index()));
        Set<Integer> myFavArticleIds = myFavorites.stream()
                .map(Favorite::getTargetId).filter(Objects::nonNull).collect(Collectors.toSet());
        myArticleIds.addAll(myFavArticleIds);

        // 2. 获取当前用户评论过的文章ID
        List<ArticleComment> myComments = articleCommentMapper.selectList(
                Wrappers.<ArticleComment>lambdaQuery().eq(ArticleComment::getUserId, userId));
        Set<Integer> myCommentArticleIds = myComments.stream()
                .map(ArticleComment::getArticleId).filter(Objects::nonNull).collect(Collectors.toSet());
        myArticleIds.addAll(myCommentArticleIds);

        // 排除当前文章
        if (currentArticleId != null)
            myArticleIds.add(currentArticleId);

        // 3. 找到与当前用户有相似行为的用户（收藏了相同文章的其他用户）
        Set<Integer> similarUserIds = new HashSet<>();
        if (!myFavArticleIds.isEmpty()) {
            List<Favorite> similarFavs = favoriteMapper.selectList(
                    Wrappers.<Favorite>lambdaQuery()
                            .eq(Favorite::getFavoriteType, FavoriteTypeEnum.科普文章.index())
                            .in(Favorite::getTargetId, myFavArticleIds)
                            .ne(Favorite::getUserId, userId));
            similarUserIds.addAll(
                    similarFavs.stream().map(Favorite::getUserId).filter(Objects::nonNull).collect(Collectors.toSet()));
        }
        // 评论了相同文章的其他用户
        if (!myCommentArticleIds.isEmpty()) {
            List<ArticleComment> similarComments = articleCommentMapper.selectList(
                    Wrappers.<ArticleComment>lambdaQuery()
                            .in(ArticleComment::getArticleId, myCommentArticleIds)
                            .ne(ArticleComment::getUserId, userId));
            similarUserIds.addAll(similarComments.stream().map(ArticleComment::getUserId).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
        }

        // 4. 获取相似用户收藏/评论过的文章，统计推荐分数
        Map<Integer, Integer> articleScoreMap = new HashMap<>();
        if (!similarUserIds.isEmpty()) {
            // 相似用户收藏的文章
            List<Favorite> simFavs = favoriteMapper.selectList(
                    Wrappers.<Favorite>lambdaQuery()
                            .eq(Favorite::getFavoriteType, FavoriteTypeEnum.科普文章.index())
                            .in(Favorite::getUserId, similarUserIds));
            for (Favorite f : simFavs) {
                if (f.getTargetId() != null && !myArticleIds.contains(f.getTargetId())) {
                    articleScoreMap.merge(f.getTargetId(), 2, Integer::sum); // 收藏权重2
                }
            }
            // 相似用户评论的文章
            List<ArticleComment> simComments = articleCommentMapper.selectList(
                    Wrappers.<ArticleComment>lambdaQuery()
                            .in(ArticleComment::getUserId, similarUserIds));
            for (ArticleComment c : simComments) {
                if (c.getArticleId() != null && !myArticleIds.contains(c.getArticleId())) {
                    articleScoreMap.merge(c.getArticleId(), 1, Integer::sum); // 评论权重1
                }
            }
        }

        // 按分数排序取Top6
        List<Integer> topIds = articleScoreMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(6)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        recommendIds.addAll(topIds);

        // 5. 兜底：如果推荐不足6篇，用同分类热门文章补充
        if (recommendIds.size() < 6 && currentArticleId != null) {
            Article currentArticle = articleMapper.selectById(currentArticleId);
            if (currentArticle != null && currentArticle.getArticleTypeId() != null) {
                List<Article> sameTypeArticles = articleMapper.selectList(
                        Wrappers.<Article>lambdaQuery()
                                .eq(Article::getArticleTypeId, currentArticle.getArticleTypeId())
                                .ne(Article::getId, currentArticleId)
                                .orderByDesc(Article::getViewCount)
                                .last("LIMIT 10"));
                for (Article a : sameTypeArticles) {
                    if (recommendIds.size() >= 6)
                        break;
                    if (!myArticleIds.contains(a.getId())) {
                        recommendIds.add(a.getId());
                    }
                }
            }
        }

        // 6. 再不足则用全站热门补充
        if (recommendIds.size() < 6) {
            List<Article> hotArticles = articleMapper.selectList(
                    Wrappers.<Article>lambdaQuery()
                            .orderByDesc(Article::getViewCount)
                            .last("LIMIT 10"));
            for (Article a : hotArticles) {
                if (recommendIds.size() >= 6)
                    break;
                if (currentArticleId != null && a.getId().equals(currentArticleId))
                    continue;
                recommendIds.add(a.getId());
            }
        }

        // 查询文章详情
        if (recommendIds.isEmpty())
            return new ArrayList<>();
        List<Article> articles = articleMapper.selectBatchIds(recommendIds);
        List<ArticleDto> result = Extension.copyBeanList(articles, ArticleDto.class);
        dispatchItem(result);
        return result;
    }
}
