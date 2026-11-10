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
 * 评分规则类
 */
@Data
public class ScoreRuleDto extends BaseDto
{
 
    private Integer scaleInfoId;// 所属量表
 
    private Integer minScore;// 最低分
 
    private Integer maxScore;// 最高分
 
    private String resultLabel;// 结果标签
 
    private String resultInterpretation;// 结果解读
 
    private String suggestion;// 建议
    private ScaleInfoDto scaleInfoDto;                        
   
    /**
     * 把评分规则传输模型转换成评分规则实体
     */
    public ScoreRule mapToEntity() throws InvocationTargetException, IllegalAccessException {
        ScoreRule scoreRule= new ScoreRule();
     
         BeanUtils.copyProperties(scoreRule,this);
        
        return scoreRule;
    }

}
