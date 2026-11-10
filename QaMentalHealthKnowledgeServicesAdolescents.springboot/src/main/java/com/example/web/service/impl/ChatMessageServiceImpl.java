package com.example.web.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.SysConst;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.enums.*;
import com.example.web.service.*;
import com.example.web.common.*;
import com.example.web.exception.CustomException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import lombok.SneakyThrows;
import java.io.IOException;
import com.example.web.utils.*;
import java.text.DecimalFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * 对话消息功能实现类
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的ChatMessage表mapper对象
     */
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private ChatSessionMapper  chatSessionMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<ChatMessage> builderQuery(ChatMessagePagedInput input) {
       //声明一个支持对话消息查询的(拉姆达)表达式
        LambdaQueryWrapper<ChatMessage> queryWrapper = Wrappers.<ChatMessage>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, ChatMessage::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getRetrievedChunkIds())) {
             queryWrapper = queryWrapper.like(ChatMessage::getRetrievedChunkIds, input.getRetrievedChunkIds());
       	 }

        if (input.getChatSessionId() != null) {
            queryWrapper = queryWrapper.eq(ChatMessage::getChatSessionId, input.getChatSessionId());
       	 }

        if (input.getSenderRole() != null) {
            queryWrapper = queryWrapper.eq(ChatMessage::getSenderRole, input.getSenderRole());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(ChatMessage::getRetrievedChunkIds,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理对话消息对于的外键数据
     */
   private List<ChatMessageDto> dispatchItem(List<ChatMessageDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (ChatMessageDto item : items) {           
          	            
           //查询出关联的ChatSession表信息           
            ChatSession  chatSession= chatSessionMapper.selectById(item.getChatSessionId());
            item.setChatSessionDto(chatSession!=null?chatSession.mapToDto():new ChatSessionDto());              
       }
       
     return items; 
   }
  
    /**
     * 对话消息分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<ChatMessageDto> list(ChatMessagePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<ChatMessage> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(ChatMessage::getCreationTime);
        }

        //构建一个分页查询的model
        Page<ChatMessage> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取对话消息数据
        IPage<ChatMessage> pageRecords= chatMessageMapper.selectPage(page, queryWrapper);
      
        //把ChatMessage实体转换成ChatMessage传输模型
        List<ChatMessageDto> items= Extension.copyBeanList(pageRecords.getRecords(),ChatMessageDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个对话消息查询
     */
    @SneakyThrows
    @Override
    public ChatMessageDto findById(ChatMessagePagedInput input) {
       if(input.getId()==null)
        {
         return new ChatMessageDto();
        }
      
       PagedResult<ChatMessageDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new ChatMessageDto()); 
    }

    /**
     *对话消息创建或者修改
     */
    @SneakyThrows
    @Override
    public ChatMessageDto addOrEdit(ChatMessageDto input) {
        //声明一个对话消息实体
        ChatMessage chatMessage=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(chatMessage);
        //把传输模型返回给前端
        return chatMessage.mapToDto();
    }
    /**
     * 对话消息删除
     */
    @Override
    public void deleteById(IdInput input) {
        ChatMessage entity = chatMessageMapper.selectById(input.getId());
        chatMessageMapper.deleteById(entity);
    }

    /**
     * 对话消息批量删除
     */
    @Override
    public void batchDeleteByIds(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            deleteById(idInput);
        }
    }
}
