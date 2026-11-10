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
 * 科普文章表
 */
@Data
public class Article extends BaseEntity {

      
    private Integer articleTypeId;// 文章分类
      
    private String title;// 文章标题
      
    private String coverImage;// 封面图片
      
    private String articleContent;// 文章内容
      
    private Integer publisherId;// 发布人
      
 	 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;// 发布时间
      
    private Integer viewCount;// 浏览量
  
    /**
     * 把科普文章实体转换成科普文章传输模型
     */
    public ArticleDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        ArticleDto articleDto = new ArticleDto();
       
        BeanUtils.copyProperties(articleDto,this);
       
        return articleDto;
    }

}
