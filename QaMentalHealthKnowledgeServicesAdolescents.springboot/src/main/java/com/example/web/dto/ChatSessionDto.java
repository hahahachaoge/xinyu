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
 * 对话会话类
 */
@Data
public class ChatSessionDto extends BaseDto
{
 
    private Integer userId;// 用户
 
    private String sessionTitle;// 会话标题
 
    private Integer messageCount;// 消息数量
 
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastMessageTime;// 最后消息时间
    private SysUserDto userDto;                        
   
    /**
     * 把对话会话传输模型转换成对话会话实体
     */
    public ChatSession mapToEntity() throws InvocationTargetException, IllegalAccessException {
        ChatSession chatSession= new ChatSession();
     
         BeanUtils.copyProperties(chatSession,this);
        
        return chatSession;
    }

}
