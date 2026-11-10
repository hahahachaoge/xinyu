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
 * 知识分类表
 */
@Data
public class KnowledgeType extends BaseEntity {

      
    private String typeName;// 分类名称
      
    private String typeDesc;// 分类描述
  
    /**
     * 把知识分类实体转换成知识分类传输模型
     */
    public KnowledgeTypeDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        KnowledgeTypeDto knowledgeTypeDto = new KnowledgeTypeDto();
       
        BeanUtils.copyProperties(knowledgeTypeDto,this);
       
        return knowledgeTypeDto;
    }

}
