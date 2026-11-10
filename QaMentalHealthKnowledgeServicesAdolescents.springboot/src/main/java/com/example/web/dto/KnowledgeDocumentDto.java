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
 * 知识文档类
 */
@Data
public class KnowledgeDocumentDto extends BaseDto
{
 
    private Integer knowledgeTypeId;// 知识分类
 
    private String docTitle;// 文档标题
 
    private String docDesc;// 文档描述
 
    private String fileUrl;// 文件路径
 
    private String fileFormat;// 文件格式
 
    private String fileSize;// 文件大小
 
    private Integer chunkCount;// 分块数量
 
    private Integer vectorStatusType;// 向量化状态
    
    public String getVectorStatusTypeFormat() {
        return VectorStatusTypeEnum.getEnum(vectorStatusType).toString();
    }
    
    private String vectorStatusTypeFormat;
    
 
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime vectorTime;// 向量化完成时间
 
    private Integer uploaderId;// 上传人
    private SysUserDto uploaderDto;                        
   
    private KnowledgeTypeDto knowledgeTypeDto;                        
   
    /**
     * 把知识文档传输模型转换成知识文档实体
     */
    public KnowledgeDocument mapToEntity() throws InvocationTargetException, IllegalAccessException {
        KnowledgeDocument knowledgeDocument= new KnowledgeDocument();
     
         BeanUtils.copyProperties(knowledgeDocument,this);
        
        return knowledgeDocument;
    }

}
