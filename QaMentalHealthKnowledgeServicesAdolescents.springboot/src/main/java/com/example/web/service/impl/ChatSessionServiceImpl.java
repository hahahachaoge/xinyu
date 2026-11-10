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
 * 对话会话功能实现类
 */
@Service
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatSessionService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的ChatSession表mapper对象
     */
    @Autowired
    private ChatSessionMapper chatSessionMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<ChatSession> builderQuery(ChatSessionPagedInput input) {
       //声明一个支持对话会话查询的(拉姆达)表达式
        LambdaQueryWrapper<ChatSession> queryWrapper = Wrappers.<ChatSession>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, ChatSession::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getSessionTitle())) {
             queryWrapper = queryWrapper.like(ChatSession::getSessionTitle, input.getSessionTitle());
       	 }

        if (input.getUserId() != null) {
            queryWrapper = queryWrapper.eq(ChatSession::getUserId, input.getUserId());
       	 }
        if (input.getLastMessageTimeRange() != null && !input.getLastMessageTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(ChatSession::getLastMessageTime, input.getLastMessageTimeRange().get(1));
            queryWrapper = queryWrapper.ge(ChatSession::getLastMessageTime, input.getLastMessageTimeRange().get(0));
        }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(ChatSession::getSessionTitle,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理对话会话对于的外键数据
     */
   private List<ChatSessionDto> dispatchItem(List<ChatSessionDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (ChatSessionDto item : items) {           
          	            
           //查询出关联的SysUser表信息           
            SysUser  user= sysUserMapper.selectById(item.getUserId());
            item.setUserDto(user!=null?user.mapToDto():new SysUserDto());              
       }
       
     return items; 
   }
  
    /**
     * 对话会话分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<ChatSessionDto> list(ChatSessionPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<ChatSession> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(ChatSession::getCreationTime);
        }

        //构建一个分页查询的model
        Page<ChatSession> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取对话会话数据
        IPage<ChatSession> pageRecords= chatSessionMapper.selectPage(page, queryWrapper);
      
        //把ChatSession实体转换成ChatSession传输模型
        List<ChatSessionDto> items= Extension.copyBeanList(pageRecords.getRecords(),ChatSessionDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个对话会话查询
     */
    @SneakyThrows
    @Override
    public ChatSessionDto findById(ChatSessionPagedInput input) {
       if(input.getId()==null)
        {
         return new ChatSessionDto();
        }
      
       PagedResult<ChatSessionDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new ChatSessionDto()); 
    }

    /**
     *对话会话创建或者修改
     */
    @SneakyThrows
    @Override
    public ChatSessionDto addOrEdit(ChatSessionDto input) {
        //声明一个对话会话实体
        ChatSession chatSession=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(chatSession);
        //把传输模型返回给前端
        return chatSession.mapToDto();
    }
    /**
     * 对话会话删除
     */
    @Override
    public void deleteById(IdInput input) {
        ChatSession entity = chatSessionMapper.selectById(input.getId());
        chatSessionMapper.deleteById(entity);
    }

    /**
     * 对话会话批量删除
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
