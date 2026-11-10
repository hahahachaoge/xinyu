package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问答反馈表对应的Mapper
 */
@Mapper
public interface QaFeedbackMapper  extends BaseMapper<QaFeedback> {

}
