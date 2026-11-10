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
 * 对话消息类
 */
@Data
public class ChatMessageDto extends BaseDto
{
 
    private Integer chatSessionId;// 所属会话
 
    private Integer senderRole;// 发送角色
    
    public String getSenderRoleFormat() {
        return SenderRoleEnum.getEnum(senderRole).toString();
    }
    
    private String senderRoleFormat;
    
 
    private String messageContent;// 消息内容
 
    private String retrievedChunkIds;// 检索分块
 
    private Integer tokenUsage;// Token消耗量
    private ChatSessionDto chatSessionDto;                        
   
    /**
     * 把对话消息传输模型转换成对话消息实体
     */
    public ChatMessage mapToEntity() throws InvocationTargetException, IllegalAccessException {
        ChatMessage chatMessage= new ChatMessage();
     
         BeanUtils.copyProperties(chatMessage,this);
        
        return chatMessage;
    }

}
