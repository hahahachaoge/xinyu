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
 * 测评量类
 */
@Data
public class ScaleInfoDto extends BaseDto
{
 
    private String scaleName;// 量表名称
 
    private String scaleDesc;// 量表描述
 
    private Integer scaleType;// 量表类型
    
    public String getScaleTypeFormat() {
        return ScaleTypeEnum.getEnum(scaleType).toString();
    }
    
    private String scaleTypeFormat;
    
 
    private String targetGroup;// 适用人群
 
    private Integer questionCount;// 题目数量
    /**
     * 把测评量传输模型转换成测评量实体
     */
    public ScaleInfo mapToEntity() throws InvocationTargetException, IllegalAccessException {
        ScaleInfo scaleInfo= new ScaleInfo();
     
         BeanUtils.copyProperties(scaleInfo,this);
        
        return scaleInfo;
    }

}
