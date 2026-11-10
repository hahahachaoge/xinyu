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
 * 文档分块表
 */
@Data
public class DocumentChunk extends BaseEntity {

      
    private Integer knowledgeDocumentId;// 所属文档
      
    private Integer chunkIndex;// 分块序号
      
    private String chunkContent;// 分块内容
      
    private Integer tokenCount;// Token数量
      
    private String vectorId;// 向量数据库
  
    /**
     * 把文档分块实体转换成文档分块传输模型
     */
    public DocumentChunkDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        DocumentChunkDto documentChunkDto = new DocumentChunkDto();
       
        BeanUtils.copyProperties(documentChunkDto,this);
       
        return documentChunkDto;
    }

}
