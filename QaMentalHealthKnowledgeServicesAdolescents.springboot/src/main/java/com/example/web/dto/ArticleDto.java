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
 * 科普文章类
 */
@Data
public class ArticleDto extends BaseDto
{
 
    private Integer articleTypeId;// 文章分类
 
    private String title;// 文章标题
 
    private String coverImage;// 封面图片
 
    private String articleContent;// 文章内容
 
    private Integer publisherId;// 发布人
 
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;// 发布时间
 
    private Integer viewCount;// 浏览量
    private SysUserDto publisherDto;                        
   
    private ArticleTypeDto articleTypeDto;                        
   
    /**
     * 把科普文章传输模型转换成科普文章实体
     */
    public Article mapToEntity() throws InvocationTargetException, IllegalAccessException {
        Article article= new Article();
     
         BeanUtils.copyProperties(article,this);
        
        return article;
    }

}
