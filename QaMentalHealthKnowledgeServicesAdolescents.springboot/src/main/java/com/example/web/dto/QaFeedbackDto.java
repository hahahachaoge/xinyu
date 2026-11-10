package com.example.web.dto;
import com.example.web.enums.*;
import com.example.web.common.*;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.sql.Date;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.example.web.entity.*;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
/**
 * 问答反馈类
 */
@Data
public class QaFeedbackDto extends BaseDto
{
 
    private Integer userId;// 用户
 
    private Integer chatMessageId;// 关联消息
 
    private Integer rating;// 评分
 
    private String feedbackContent;// 反馈内容
    private ChatMessageDto chatMessageDto;                        
   
    private SysUserDto userDto;                        
   
    /**
     * 把问答反馈传输模型转换成问答反馈实体
     */
    public QaFeedback mapToEntity() throws InvocationTargetException, IllegalAccessException {
        QaFeedback qaFeedback= new QaFeedback();
     
         BeanUtils.copyProperties(qaFeedback,this);
        
        return qaFeedback;
    }

}
