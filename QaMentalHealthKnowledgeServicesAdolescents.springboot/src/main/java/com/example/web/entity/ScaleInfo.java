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
 * 测评量表
 */
@Data
public class ScaleInfo extends BaseEntity {

      
    private String scaleName;// 量表名称
      
    private String scaleDesc;// 量表描述
      
    private Integer scaleType;// 量表类型
      
    private String targetGroup;// 适用人群
      
    private Integer questionCount;// 题目数量
  
    /**
     * 把测评量实体转换成测评量传输模型
     */
    public ScaleInfoDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        ScaleInfoDto scaleInfoDto = new ScaleInfoDto();
       
        BeanUtils.copyProperties(scaleInfoDto,this);
       
        return scaleInfoDto;
    }

}
