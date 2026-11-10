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
 * 公告信息类
 */
@Data
public class NoticeDto extends BaseDto
{
 
    private Integer noticeTypeId;// 公告分类
 
    private String title;// 公告标题
 
    private String noticeContent;// 公告内容
 
    private Integer publisherId;// 发布人
 
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;// 发布时间
    private SysUserDto publisherDto;                        
   
    private NoticeTypeDto noticeTypeDto;                        
   
    /**
     * 把公告信息传输模型转换成公告信息实体
     */
    public Notice mapToEntity() throws InvocationTargetException, IllegalAccessException {
        Notice notice= new Notice();
     
         BeanUtils.copyProperties(notice,this);
        
        return notice;
    }

}
