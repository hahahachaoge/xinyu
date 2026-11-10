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
 * 测评记录类
 */
@Data
public class AssessmentRecordDto extends BaseDto
{
 
    private Integer userId;// 用户
 
    private Integer scaleInfoId;// 测评量表
 
    private Integer totalScore;// 总分
 
    private String resultLabel;// 结果标签
 
    private String suggestion;

    private String resultInterpretation;// 结果解读
 
    private String answerJson;// 作答详情JSON
 
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;// 完成时间
    private ScaleInfoDto scaleInfoDto;                        
   
    private SysUserDto userDto;                        
   
    /**
     * 把测评记录传输模型转换成测评记录实体
     */
    public AssessmentRecord mapToEntity() throws InvocationTargetException, IllegalAccessException {
        AssessmentRecord assessmentRecord= new AssessmentRecord();
     
         BeanUtils.copyProperties(assessmentRecord,this);
        
        return assessmentRecord;
    }

}
