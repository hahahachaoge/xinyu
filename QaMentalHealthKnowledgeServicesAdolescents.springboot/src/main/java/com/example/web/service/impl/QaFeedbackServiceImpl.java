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
 * 问答反馈功能实现类
 */
@Service
public class QaFeedbackServiceImpl extends ServiceImpl<QaFeedbackMapper, QaFeedback> implements QaFeedbackService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的QaFeedback表mapper对象
     */
    @Autowired
    private QaFeedbackMapper qaFeedbackMapper;
    @Autowired
    private ChatMessageMapper  chatMessageMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<QaFeedback> builderQuery(QaFeedbackPagedInput input) {
       //声明一个支持问答反馈查询的(拉姆达)表达式
        LambdaQueryWrapper<QaFeedback> queryWrapper = Wrappers.<QaFeedback>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, QaFeedback::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件

        if (input.getUserId() != null) {
            queryWrapper = queryWrapper.eq(QaFeedback::getUserId, input.getUserId());
       	 }

        if (input.getChatMessageId() != null) {
            queryWrapper = queryWrapper.eq(QaFeedback::getChatMessageId, input.getChatMessageId());
       	 }
      

 
    
      return queryWrapper;
    }
    /**
     * 处理问答反馈对于的外键数据
     */
   private List<QaFeedbackDto> dispatchItem(List<QaFeedbackDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (QaFeedbackDto item : items) {           
          	            
           //查询出关联的ChatMessage表信息           
            ChatMessage  chatMessage= chatMessageMapper.selectById(item.getChatMessageId());
            item.setChatMessageDto(chatMessage!=null?chatMessage.mapToDto():new ChatMessageDto());              
           
          	            
           //查询出关联的SysUser表信息           
            SysUser  user= sysUserMapper.selectById(item.getUserId());
            item.setUserDto(user!=null?user.mapToDto():new SysUserDto());              
       }
       
     return items; 
   }
  
    /**
     * 问答反馈分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<QaFeedbackDto> list(QaFeedbackPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<QaFeedback> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(QaFeedback::getCreationTime);
        }

        //构建一个分页查询的model
        Page<QaFeedback> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取问答反馈数据
        IPage<QaFeedback> pageRecords= qaFeedbackMapper.selectPage(page, queryWrapper);
      
        //把QaFeedback实体转换成QaFeedback传输模型
        List<QaFeedbackDto> items= Extension.copyBeanList(pageRecords.getRecords(),QaFeedbackDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个问答反馈查询
     */
    @SneakyThrows
    @Override
    public QaFeedbackDto findById(QaFeedbackPagedInput input) {
       if(input.getId()==null)
        {
         return new QaFeedbackDto();
        }
      
       PagedResult<QaFeedbackDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new QaFeedbackDto()); 
    }

    /**
     *问答反馈创建或者修改
     */
    @SneakyThrows
    @Override
    public QaFeedbackDto addOrEdit(QaFeedbackDto input) {
        //声明一个问答反馈实体
        QaFeedback qaFeedback=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(qaFeedback);
        //把传输模型返回给前端
        return qaFeedback.mapToDto();
    }
    /**
     * 问答反馈删除
     */
    @Override
    public void deleteById(IdInput input) {
        QaFeedback entity = qaFeedbackMapper.selectById(input.getId());
        qaFeedbackMapper.deleteById(entity);
    }

    /**
     * 问答反馈批量删除
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
