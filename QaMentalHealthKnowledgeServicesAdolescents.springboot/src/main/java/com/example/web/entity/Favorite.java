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
 * 收藏表
 */
@Data
public class Favorite extends BaseEntity {

      
    private Integer userId;// 用户
      
    private Integer favoriteType;// 收藏类型
      
    private Integer targetId;// 收藏目标
  
    /**
     * 把收藏实体转换成收藏传输模型
     */
    public FavoriteDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        FavoriteDto favoriteDto = new FavoriteDto();
       
        BeanUtils.copyProperties(favoriteDto,this);
       
        return favoriteDto;
    }

}
