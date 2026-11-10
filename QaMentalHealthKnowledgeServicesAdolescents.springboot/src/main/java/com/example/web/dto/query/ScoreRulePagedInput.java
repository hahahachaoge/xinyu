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
 * 评分规则查询模型
 */
@NoArgsConstructor
@Data
public class ScoreRulePagedInput extends PagedInput {
    
    private Integer id;// Id主键
    private String resultLabel;// 结果标签模糊查询条件
    private Integer scaleInfoId;// 所属量表

}
