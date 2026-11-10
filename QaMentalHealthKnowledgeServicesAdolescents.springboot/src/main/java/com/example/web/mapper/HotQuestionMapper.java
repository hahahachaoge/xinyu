package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 热门问题表对应的Mapper
 */
@Mapper
public interface HotQuestionMapper  extends BaseMapper<HotQuestion> {

}
