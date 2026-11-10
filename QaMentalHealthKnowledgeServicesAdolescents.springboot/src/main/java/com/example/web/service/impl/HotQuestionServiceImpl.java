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
 * 热门问题功能实现类
 */
@Service
public class HotQuestionServiceImpl extends ServiceImpl<HotQuestionMapper, HotQuestion> implements HotQuestionService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的HotQuestion表mapper对象
     */
    @Autowired
    private HotQuestionMapper hotQuestionMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<HotQuestion> builderQuery(HotQuestionPagedInput input) {
       //声明一个支持热门问题查询的(拉姆达)表达式
        LambdaQueryWrapper<HotQuestion> queryWrapper = Wrappers.<HotQuestion>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, HotQuestion::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getQuestionText())) {
             queryWrapper = queryWrapper.like(HotQuestion::getQuestionText, input.getQuestionText());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(HotQuestion::getQuestionText,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理热门问题对于的外键数据
     */
   private List<HotQuestionDto> dispatchItem(List<HotQuestionDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (HotQuestionDto item : items) {       }
       
     return items; 
   }
  
    /**
     * 热门问题分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<HotQuestionDto> list(HotQuestionPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<HotQuestion> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(HotQuestion::getCreationTime);
        }

        //构建一个分页查询的model
        Page<HotQuestion> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取热门问题数据
        IPage<HotQuestion> pageRecords= hotQuestionMapper.selectPage(page, queryWrapper);
      
        //把HotQuestion实体转换成HotQuestion传输模型
        List<HotQuestionDto> items= Extension.copyBeanList(pageRecords.getRecords(),HotQuestionDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个热门问题查询
     */
    @SneakyThrows
    @Override
    public HotQuestionDto findById(HotQuestionPagedInput input) {
       if(input.getId()==null)
        {
         return new HotQuestionDto();
        }
      
       PagedResult<HotQuestionDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new HotQuestionDto()); 
    }

    /**
     *热门问题创建或者修改
     */
    @SneakyThrows
    @Override
    public HotQuestionDto addOrEdit(HotQuestionDto input) {
        //声明一个热门问题实体
        HotQuestion hotQuestion=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(hotQuestion);
        //把传输模型返回给前端
        return hotQuestion.mapToDto();
    }
    /**
     * 热门问题删除
     */
    @Override
    public void deleteById(IdInput input) {
        HotQuestion entity = hotQuestionMapper.selectById(input.getId());
        hotQuestionMapper.deleteById(entity);
    }

    /**
     * 热门问题批量删除
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
