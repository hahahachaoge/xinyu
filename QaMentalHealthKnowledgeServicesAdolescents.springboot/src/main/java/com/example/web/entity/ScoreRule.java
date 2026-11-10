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
 * 评分规则表
 */
@Data
public class ScoreRule extends BaseEntity {

      
    private Integer scaleInfoId;// 所属量表
      
    private Integer minScore;// 最低分
      
    private Integer maxScore;// 最高分
      
    private String resultLabel;// 结果标签
      
    private String resultInterpretation;// 结果解读
      
    private String suggestion;// 建议
  
    /**
     * 把评分规则实体转换成评分规则传输模型
     */
    public ScoreRuleDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        ScoreRuleDto scoreRuleDto = new ScoreRuleDto();
       
        BeanUtils.copyProperties(scoreRuleDto,this);
       
        return scoreRuleDto;
    }

}
