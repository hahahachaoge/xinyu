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
 * 测评题目类
 */
@Data
public class ScaleQuestionDto extends BaseDto
{
 
    private Integer scaleInfoId;// 所属量表
 
    private Integer questionIndex;// 题目序号
 
    private String questionTitle;// 题目标题
 
    private Integer questionType;// 题目类型
    
    public String getQuestionTypeFormat() {
        return QuestionTypeEnum.getEnum(questionType).toString();
    }
    
    private String questionTypeFormat;
    
 
    private String optionJson;// 选项与分值JSON
    private ScaleInfoDto scaleInfoDto;                        
   
    /**
     * 把测评题目传输模型转换成测评题目实体
     */
    public ScaleQuestion mapToEntity() throws InvocationTargetException, IllegalAccessException {
        ScaleQuestion scaleQuestion= new ScaleQuestion();
     
         BeanUtils.copyProperties(scaleQuestion,this);
        
        return scaleQuestion;
    }

}
