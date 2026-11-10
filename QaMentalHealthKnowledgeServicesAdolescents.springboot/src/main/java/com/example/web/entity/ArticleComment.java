package com.example.web.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.sql.Date;
import java.sql.Timestamp;
import lombok.Data;
import java.time.LocalDateTime;
import com.example.web.dto.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import com.baomidou.mybatisplus.annotation.FieldStrategy;

/**
 * 文章评论表
 */
@Data
public class ArticleComment extends BaseEntity {

    private Integer articleId;// 关联文章

    private Integer userId;// 评论者

    private String commentContent;// 评论内容

    private Integer parentId;// 父评论ID（回复哪条评论）

    private Integer replyToUserId;// 回复目标用户ID

    /**
     * 把文章评论实体转换成文章评论传输模型
     */
    public ArticleCommentDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        ArticleCommentDto articleCommentDto = new ArticleCommentDto();

        BeanUtils.copyProperties(articleCommentDto, this);

        return articleCommentDto;
    }

}
