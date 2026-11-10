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
 * 文档分块类
 */
@Data
public class DocumentChunkDto extends BaseDto
{
 
    private Integer knowledgeDocumentId;// 所属文档
 
    private Integer chunkIndex;// 分块序号
 
    private String chunkContent;// 分块内容
 
    private Integer tokenCount;// Token数量
 
    private String vectorId;// 向量数据库
    private KnowledgeDocumentDto knowledgeDocumentDto;                        
   
    /**
     * 把文档分块传输模型转换成文档分块实体
     */
    public DocumentChunk mapToEntity() throws InvocationTargetException, IllegalAccessException {
        DocumentChunk documentChunk= new DocumentChunk();
     
         BeanUtils.copyProperties(documentChunk,this);
        
        return documentChunk;
    }

}
