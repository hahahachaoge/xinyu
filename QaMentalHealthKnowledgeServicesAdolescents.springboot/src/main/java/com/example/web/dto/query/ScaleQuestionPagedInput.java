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
 * 测评题目查询模型
 */
@NoArgsConstructor
@Data
public class ScaleQuestionPagedInput extends PagedInput {
    
    private Integer id;// Id主键
    private String questionTitle;// 题目标题模糊查询条件
    private Integer scaleInfoId;// 所属量表
    private Integer questionType;// 题目类型

}
