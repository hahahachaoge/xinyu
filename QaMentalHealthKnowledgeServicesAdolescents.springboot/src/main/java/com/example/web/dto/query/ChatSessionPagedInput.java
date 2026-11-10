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
 * 对话会话查询模型
 */
@NoArgsConstructor
@Data
public class ChatSessionPagedInput extends PagedInput {
    
    private Integer id;// Id主键
    private String sessionTitle;// 会话标题模糊查询条件
    private Integer userId;// 用户
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private List<LocalDateTime> lastMessageTimeRange;// 最后消息时间时间范围

}
