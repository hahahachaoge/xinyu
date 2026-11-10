package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评分规则表对应的Mapper
 */
@Mapper
public interface ScoreRuleMapper  extends BaseMapper<ScoreRule> {

}
