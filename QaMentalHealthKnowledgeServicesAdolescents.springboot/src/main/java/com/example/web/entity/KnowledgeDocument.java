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
 * 知识文档表
 */
@Data
public class KnowledgeDocument extends BaseEntity {

      
    private Integer knowledgeTypeId;// 知识分类
      
    private String docTitle;// 文档标题
      
    private String docDesc;// 文档描述
      
    private String fileUrl;// 文件路径
      
    private String fileFormat;// 文件格式
      
    private String fileSize;// 文件大小
      
    private Integer chunkCount;// 分块数量
      
    private Integer vectorStatusType;// 向量化状态
      
 	 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime vectorTime;// 向量化完成时间
      
    private Integer uploaderId;// 上传人
  
    /**
     * 把知识文档实体转换成知识文档传输模型
     */
    public KnowledgeDocumentDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        KnowledgeDocumentDto knowledgeDocumentDto = new KnowledgeDocumentDto();
       
        BeanUtils.copyProperties(knowledgeDocumentDto,this);
       
        return knowledgeDocumentDto;
    }

}
