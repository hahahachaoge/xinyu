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
 * 公告信息表
 */
@Data
public class Notice extends BaseEntity {

      
    private Integer noticeTypeId;// 公告分类
      
    private String title;// 公告标题
      
    private String noticeContent;// 公告内容
      
    private Integer publisherId;// 发布人
      
 	 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;// 发布时间
  
    /**
     * 把公告信息实体转换成公告信息传输模型
     */
    public NoticeDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        NoticeDto noticeDto = new NoticeDto();
       
        BeanUtils.copyProperties(noticeDto,this);
       
        return noticeDto;
    }

}
