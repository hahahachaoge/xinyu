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
 * 对话消息表
 */
@Data
public class ChatMessage extends BaseEntity {

      
    private Integer chatSessionId;// 所属会话
      
    private Integer senderRole;// 发送角色
      
    private String messageContent;// 消息内容
      
    private String retrievedChunkIds;// 检索分块
      
    private Integer tokenUsage;// Token消耗量
  
    /**
     * 把对话消息实体转换成对话消息传输模型
     */
    public ChatMessageDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        ChatMessageDto chatMessageDto = new ChatMessageDto();
       
        BeanUtils.copyProperties(chatMessageDto,this);
       
        return chatMessageDto;
    }

}
