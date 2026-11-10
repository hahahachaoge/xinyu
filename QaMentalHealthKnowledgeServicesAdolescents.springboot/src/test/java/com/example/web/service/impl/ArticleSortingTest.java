package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.web.common.SortItemDto;
import com.example.web.dto.query.ArticlePagedInput;
import com.example.web.entity.Article;
import com.example.web.mapper.ArticleMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ArticleSortingTest {
    private ArticleMapper mapper;
    private ArticleServiceImpl service;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Article.class);
        mapper = mock(ArticleMapper.class);
        service = new ArticleServiceImpl();
        ReflectionTestUtils.setField(service, "articleMapper", mapper);
        doAnswer(invocation -> invocation.getArgument(0)).when(mapper).selectPage(any(Page.class), any());
    }

    @ParameterizedTest
    @CsvSource({
            "viewCount,false,view_count DESC",
            "view_count,true,view_count ASC",
            "publishTime,false,publish_time DESC",
            "publish_time,true,publish_time ASC",
            "creationTime,false,creation_time DESC",
            "title,true,title ASC"
    })
    void mapsPropertyAndColumnNames(String field, boolean ascending, String expectedOrder) {
        SortItemDto sort = new SortItemDto();
        sort.setFieldName(field);
        sort.setIsAsc(ascending);
        assertOrder(sort, expectedOrder);
    }

    @Test
    void defaultsMissingDirectionToDescending() {
        SortItemDto sort = new SortItemDto();
        sort.setFieldName("viewCount");
        assertOrder(sort, "view_count DESC");
    }

    @Test
    void defaultsMissingSortToCreationTime() {
        assertOrder(null, "creation_time DESC");
    }

    @Test
    void ignoresUnknownFieldsInsteadOfConcatenatingSql() {
        SortItemDto sort = new SortItemDto();
        sort.setFieldName("viewCount DESC, nonexistent_column");
        sort.setIsAsc(true);
        assertOrder(sort, "creation_time DESC");
    }

    @Test
    void handlesEmptySortObjects() {
        assertOrder(new SortItemDto(), "creation_time DESC");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void assertOrder(SortItemDto sort, String expectedOrder) {
        ArticlePagedInput input = new ArticlePagedInput();
        input.setSortItem(sort);
        service.list(input);
        ArgumentCaptor<LambdaQueryWrapper<Article>> query = ArgumentCaptor.forClass((Class) LambdaQueryWrapper.class);
        verify(mapper).selectPage(any(Page.class), query.capture());
        assertTrue(query.getValue().getSqlSegment().endsWith("ORDER BY " + expectedOrder), query.getValue().getSqlSegment());
    }
}
