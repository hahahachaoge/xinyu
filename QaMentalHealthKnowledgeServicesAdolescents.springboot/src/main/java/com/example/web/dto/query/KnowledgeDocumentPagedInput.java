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
 * 知识文档查询模型
 */
@NoArgsConstructor
@Data
public class KnowledgeDocumentPagedInput extends PagedInput {
    
    private Integer id;// Id主键
    private String docTitle;// 文档标题模糊查询条件
    private String fileFormat;// 文件格式模糊查询条件
    private String fileSize;// 文件大小模糊查询条件
    private Integer knowledgeTypeId;// 知识分类
    private Integer vectorStatusType;// 向量化状态
    private Integer uploaderId;// 上传人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private List<LocalDateTime> vectorTimeRange;// 向量化完成时间时间范围

}
