package com.example.web.dto.query;

import com.example.web.common.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * 对话消息查询模型
 */
@NoArgsConstructor
@Data
public class ChatMessagePagedInput extends PagedInput {
    
    private Integer id;// Id主键
    private String retrievedChunkIds;// 检索分块模糊查询条件
    private Integer chatSessionId;// 所属会话
    private Integer senderRole;// 发送角色

}
