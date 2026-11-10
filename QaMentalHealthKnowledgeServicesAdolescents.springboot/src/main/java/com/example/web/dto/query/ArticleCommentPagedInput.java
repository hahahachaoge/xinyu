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
 * 文章评论查询模型
 */
@NoArgsConstructor
@Data
public class ArticleCommentPagedInput extends PagedInput {
    
    private Integer id;// Id主键
    private Integer articleId;// 关联文章
    private Integer userId;// 评论者

}
