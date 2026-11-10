package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识文档表对应的Mapper
 */
@Mapper
public interface KnowledgeDocumentMapper  extends BaseMapper<KnowledgeDocument> {

}
