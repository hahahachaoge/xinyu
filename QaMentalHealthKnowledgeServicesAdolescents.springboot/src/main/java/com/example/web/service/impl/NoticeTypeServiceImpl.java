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
 * 公告分类功能实现类
 */
@Service
public class NoticeTypeServiceImpl extends ServiceImpl<NoticeTypeMapper, NoticeType> implements NoticeTypeService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的NoticeType表mapper对象
     */
    @Autowired
    private NoticeTypeMapper noticeTypeMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<NoticeType> builderQuery(NoticeTypePagedInput input) {
       //声明一个支持公告分类查询的(拉姆达)表达式
        LambdaQueryWrapper<NoticeType> queryWrapper = Wrappers.<NoticeType>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, NoticeType::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getTypeName())) {
             queryWrapper = queryWrapper.like(NoticeType::getTypeName, input.getTypeName());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(NoticeType::getTypeName,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理公告分类对于的外键数据
     */
   private List<NoticeTypeDto> dispatchItem(List<NoticeTypeDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (NoticeTypeDto item : items) {       }
       
     return items; 
   }
  
    /**
     * 公告分类分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<NoticeTypeDto> list(NoticeTypePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<NoticeType> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(NoticeType::getCreationTime);
        }

        //构建一个分页查询的model
        Page<NoticeType> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取公告分类数据
        IPage<NoticeType> pageRecords= noticeTypeMapper.selectPage(page, queryWrapper);
      
        //把NoticeType实体转换成NoticeType传输模型
        List<NoticeTypeDto> items= Extension.copyBeanList(pageRecords.getRecords(),NoticeTypeDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个公告分类查询
     */
    @SneakyThrows
    @Override
    public NoticeTypeDto findById(NoticeTypePagedInput input) {
       if(input.getId()==null)
        {
         return new NoticeTypeDto();
        }
      
       PagedResult<NoticeTypeDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new NoticeTypeDto()); 
    }

    /**
     *公告分类创建或者修改
     */
    @SneakyThrows
    @Override
    public NoticeTypeDto addOrEdit(NoticeTypeDto input) {
        //声明一个公告分类实体
        NoticeType noticeType=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(noticeType);
        //把传输模型返回给前端
        return noticeType.mapToDto();
    }
    /**
     * 公告分类删除
     */
    @Override
    public void deleteById(IdInput input) {
        NoticeType entity = noticeTypeMapper.selectById(input.getId());
        noticeTypeMapper.deleteById(entity);
    }

    /**
     * 公告分类批量删除
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
