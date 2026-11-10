package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章分类表对应的Mapper
 */
@Mapper
public interface ArticleTypeMapper  extends BaseMapper<ArticleType> {

}
