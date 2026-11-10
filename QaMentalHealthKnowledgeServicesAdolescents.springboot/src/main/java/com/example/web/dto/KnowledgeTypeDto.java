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
 * 知识分类类
 */
@Data
public class KnowledgeTypeDto extends BaseDto
{
 
    private String typeName;// 分类名称
 
    private String typeDesc;// 分类描述
    /**
     * 把知识分类传输模型转换成知识分类实体
     */
    public KnowledgeType mapToEntity() throws InvocationTargetException, IllegalAccessException {
        KnowledgeType knowledgeType= new KnowledgeType();
     
         BeanUtils.copyProperties(knowledgeType,this);
        
        return knowledgeType;
    }

}
