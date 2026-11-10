package com.example.web.dto;

import com.example.web.enums.*;
import com.example.web.common.*;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.sql.Date;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.example.web.entity.*;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * 文章评论类
 */
@Data
public class ArticleCommentDto extends BaseDto {

    private Integer articleId;// 关联文章

    private Integer userId;// 评论者

    private String commentContent;// 评论内容

    private Integer parentId;// 父评论ID

    private Integer replyToUserId;// 回复目标用户ID

    private ArticleDto articleDto;

    private SysUserDto userDto;

    private SysUserDto replyToUserDto;// 回复目标用户信息

    /**
     * 把文章评论传输模型转换成文章评论实体
     */
    public ArticleComment mapToEntity() throws InvocationTargetException, IllegalAccessException {
        ArticleComment articleComment = new ArticleComment();

        BeanUtils.copyProperties(articleComment, this);

        return articleComment;
    }

}
