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
 * 公告信息功能实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的Notice表mapper对象
     */
    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private NoticeTypeMapper  noticeTypeMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<Notice> builderQuery(NoticePagedInput input) {
       //声明一个支持公告信息查询的(拉姆达)表达式
        LambdaQueryWrapper<Notice> queryWrapper = Wrappers.<Notice>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, Notice::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getTitle())) {
             queryWrapper = queryWrapper.like(Notice::getTitle, input.getTitle());
       	 }

        if (input.getNoticeTypeId() != null) {
            queryWrapper = queryWrapper.eq(Notice::getNoticeTypeId, input.getNoticeTypeId());
       	 }

        if (input.getPublisherId() != null) {
            queryWrapper = queryWrapper.eq(Notice::getPublisherId, input.getPublisherId());
       	 }
        if (input.getPublishTimeRange() != null && !input.getPublishTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(Notice::getPublishTime, input.getPublishTimeRange().get(1));
            queryWrapper = queryWrapper.ge(Notice::getPublishTime, input.getPublishTimeRange().get(0));
        }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(Notice::getTitle,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理公告信息对于的外键数据
     */
   private List<NoticeDto> dispatchItem(List<NoticeDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (NoticeDto item : items) {           
          	            
           //查询出关联的SysUser表信息           
            SysUser  publisher= sysUserMapper.selectById(item.getPublisherId());
            item.setPublisherDto(publisher!=null?publisher.mapToDto():new SysUserDto());              
           
          	            
           //查询出关联的NoticeType表信息           
            NoticeType  noticeType= noticeTypeMapper.selectById(item.getNoticeTypeId());
            item.setNoticeTypeDto(noticeType!=null?noticeType.mapToDto():new NoticeTypeDto());              
       }
       
     return items; 
   }
  
    /**
     * 公告信息分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<NoticeDto> list(NoticePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<Notice> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(Notice::getCreationTime);
        }

        //构建一个分页查询的model
        Page<Notice> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取公告信息数据
        IPage<Notice> pageRecords= noticeMapper.selectPage(page, queryWrapper);
      
        //把Notice实体转换成Notice传输模型
        List<NoticeDto> items= Extension.copyBeanList(pageRecords.getRecords(),NoticeDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个公告信息查询
     */
    @SneakyThrows
    @Override
    public NoticeDto findById(NoticePagedInput input) {
       if(input.getId()==null)
        {
         return new NoticeDto();
        }
      
       PagedResult<NoticeDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new NoticeDto()); 
    }

    /**
     *公告信息创建或者修改
     */
    @SneakyThrows
    @Override
    public NoticeDto addOrEdit(NoticeDto input) {
        //声明一个公告信息实体
        Notice notice=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(notice);
        //把传输模型返回给前端
        return notice.mapToDto();
    }
    /**
     * 公告信息删除
     */
    @Override
    public void deleteById(IdInput input) {
        Notice entity = noticeMapper.selectById(input.getId());
        noticeMapper.deleteById(entity);
    }

    /**
     * 公告信息批量删除
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
