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
 * 公告信息查询模型
 */
@NoArgsConstructor
@Data
public class NoticePagedInput extends PagedInput {
    
    private Integer id;// Id主键
    private String title;// 公告标题模糊查询条件
    private Integer noticeTypeId;// 公告分类
    private Integer publisherId;// 发布人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private List<LocalDateTime> publishTimeRange;// 发布时间时间范围

}
