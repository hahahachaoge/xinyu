package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测评题目表对应的Mapper
 */
@Mapper
public interface ScaleQuestionMapper  extends BaseMapper<ScaleQuestion> {

}
