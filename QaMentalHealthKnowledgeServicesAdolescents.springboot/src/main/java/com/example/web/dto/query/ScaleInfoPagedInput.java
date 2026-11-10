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
 * 测评量查询模型
 */
@NoArgsConstructor
@Data
public class ScaleInfoPagedInput extends PagedInput {
    
    private Integer id;// Id主键
    private String scaleName;// 量表名称模糊查询条件
    private String targetGroup;// 适用人群模糊查询条件
    private Integer scaleType;// 量表类型

}
