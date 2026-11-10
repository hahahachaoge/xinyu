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
 * 测评记录表
 */
@Data
public class AssessmentRecord extends BaseEntity {

      
    private Integer userId;// 用户
      
    private Integer scaleInfoId;// 测评量表
      
    private Integer totalScore;// 总分
      
    private String resultLabel;// 结果标签
      
    private String resultInterpretation;// 结果解读
      
    private String answerJson;// 作答详情JSON
      
 	 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;// 完成时间
  
    /**
     * 把测评记录实体转换成测评记录传输模型
     */
    public AssessmentRecordDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        AssessmentRecordDto assessmentRecordDto = new AssessmentRecordDto();
       
        BeanUtils.copyProperties(assessmentRecordDto,this);
       
        return assessmentRecordDto;
    }

}
