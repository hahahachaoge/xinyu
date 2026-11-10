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
 * 测评记录查询模型
 */
@NoArgsConstructor
@Data
public class AssessmentRecordPagedInput extends PagedInput {
    
    private Integer id;// Id主键
    private String resultLabel;// 结果标签模糊查询条件
    private Integer userId;// 用户
    private Integer scaleInfoId;// 测评量表
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private List<LocalDateTime> finishTimeRange;// 完成时间时间范围

}
