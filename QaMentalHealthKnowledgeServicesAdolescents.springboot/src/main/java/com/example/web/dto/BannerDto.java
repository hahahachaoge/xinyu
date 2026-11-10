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
 * 轮播图类
 */
@Data
public class BannerDto extends BaseDto
{
 
    private String imageUrl;// 轮播图片
 
    private String title;// 轮播标题
    /**
     * 把轮播图传输模型转换成轮播图实体
     */
    public Banner mapToEntity() throws InvocationTargetException, IllegalAccessException {
        Banner banner= new Banner();
     
         BeanUtils.copyProperties(banner,this);
        
        return banner;
    }

}
