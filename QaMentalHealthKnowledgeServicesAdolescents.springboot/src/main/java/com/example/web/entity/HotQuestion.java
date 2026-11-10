package com.example.web.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.sql.Date;
import java.sql.Timestamp;
import lombok.Data;
import java.time.LocalDateTime;
import com.example.web.dto.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
/**
 * 热门问题表
 */
@Data
public class HotQuestion extends BaseEntity {

      
    private String questionText;// 问题文本
      
    private Integer askCount;// 提问次数
  
    /**
     * 把热门问题实体转换成热门问题传输模型
     */
    public HotQuestionDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        HotQuestionDto hotQuestionDto = new HotQuestionDto();
       
        BeanUtils.copyProperties(hotQuestionDto,this);
       
        return hotQuestionDto;
    }

}
