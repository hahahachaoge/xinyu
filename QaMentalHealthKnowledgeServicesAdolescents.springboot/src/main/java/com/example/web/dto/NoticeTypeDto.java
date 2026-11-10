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
 * 公告分类类
 */
@Data
public class NoticeTypeDto extends BaseDto
{
 
    private String typeName;// 分类名称
 
    private String typeDesc;// 分类描述
    /**
     * 把公告分类传输模型转换成公告分类实体
     */
    public NoticeType mapToEntity() throws InvocationTargetException, IllegalAccessException {
        NoticeType noticeType= new NoticeType();
     
         BeanUtils.copyProperties(noticeType,this);
        
        return noticeType;
    }

}
