package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告分类表对应的Mapper
 */
@Mapper
public interface NoticeTypeMapper  extends BaseMapper<NoticeType> {

}
