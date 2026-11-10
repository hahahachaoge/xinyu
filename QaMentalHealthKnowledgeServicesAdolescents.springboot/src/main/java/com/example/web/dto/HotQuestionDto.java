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
 * 热门问题类
 */
@Data
public class HotQuestionDto extends BaseDto
{
 
    private String questionText;// 问题文本
 
    private Integer askCount;// 提问次数
    /**
     * 把热门问题传输模型转换成热门问题实体
     */
    public HotQuestion mapToEntity() throws InvocationTargetException, IllegalAccessException {
        HotQuestion hotQuestion= new HotQuestion();
     
         BeanUtils.copyProperties(hotQuestion,this);
        
        return hotQuestion;
    }

}
