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
 * 科普文章查询模型
 */
@NoArgsConstructor
@Data
public class ArticlePagedInput extends PagedInput {
    
    private Integer id;// Id主键
    private String title;// 文章标题模糊查询条件
    private Integer articleTypeId;// 文章分类
    private Integer publisherId;// 发布人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private List<LocalDateTime> publishTimeRange;// 发布时间时间范围

}
