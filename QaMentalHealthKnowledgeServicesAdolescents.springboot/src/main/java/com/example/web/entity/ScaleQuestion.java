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
 * 测评题目表
 */
@Data
public class ScaleQuestion extends BaseEntity {

      
    private Integer scaleInfoId;// 所属量表
      
    private Integer questionIndex;// 题目序号
      
    private String questionTitle;// 题目标题
      
    private Integer questionType;// 题目类型
      
    private String optionJson;// 选项与分值JSON
  
    /**
     * 把测评题目实体转换成测评题目传输模型
     */
    public ScaleQuestionDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        ScaleQuestionDto scaleQuestionDto = new ScaleQuestionDto();
       
        BeanUtils.copyProperties(scaleQuestionDto,this);
       
        return scaleQuestionDto;
    }

}
