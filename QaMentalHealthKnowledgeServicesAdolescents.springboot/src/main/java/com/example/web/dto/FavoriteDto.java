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
 * 收藏类
 */
@Data
public class FavoriteDto extends BaseDto
{
 
    private Integer userId;// 用户
 
    private Integer favoriteType;// 收藏类型
    
    public String getFavoriteTypeFormat() {
        return FavoriteTypeEnum.getEnum(favoriteType).toString();
    }
    
    private String favoriteTypeFormat;
    
 
    private Integer targetId;// 收藏目标
    private SysUserDto userDto;                        
   
    /**
     * 把收藏传输模型转换成收藏实体
     */
    public Favorite mapToEntity() throws InvocationTargetException, IllegalAccessException {
        Favorite favorite= new Favorite();
     
         BeanUtils.copyProperties(favorite,this);
        
        return favorite;
    }

}
