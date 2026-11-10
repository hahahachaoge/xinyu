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
 * 测评题目功能实现类
 */
@Service
public class ScaleQuestionServiceImpl extends ServiceImpl<ScaleQuestionMapper, ScaleQuestion> implements ScaleQuestionService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的ScaleQuestion表mapper对象
     */
    @Autowired
    private ScaleQuestionMapper scaleQuestionMapper;
    @Autowired
    private ScaleInfoMapper  scaleInfoMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<ScaleQuestion> builderQuery(ScaleQuestionPagedInput input) {
       //声明一个支持测评题目查询的(拉姆达)表达式
        LambdaQueryWrapper<ScaleQuestion> queryWrapper = Wrappers.<ScaleQuestion>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, ScaleQuestion::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getQuestionTitle())) {
             queryWrapper = queryWrapper.like(ScaleQuestion::getQuestionTitle, input.getQuestionTitle());
       	 }

        if (input.getScaleInfoId() != null) {
            queryWrapper = queryWrapper.eq(ScaleQuestion::getScaleInfoId, input.getScaleInfoId());
       	 }

        if (input.getQuestionType() != null) {
            queryWrapper = queryWrapper.eq(ScaleQuestion::getQuestionType, input.getQuestionType());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(ScaleQuestion::getQuestionTitle,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理测评题目对于的外键数据
     */
   private List<ScaleQuestionDto> dispatchItem(List<ScaleQuestionDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (ScaleQuestionDto item : items) {           
          	            
           //查询出关联的ScaleInfo表信息           
            ScaleInfo  scaleInfo= scaleInfoMapper.selectById(item.getScaleInfoId());
            item.setScaleInfoDto(scaleInfo!=null?scaleInfo.mapToDto():new ScaleInfoDto());              
       }
       
     return items; 
   }
  
    /**
     * 测评题目分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<ScaleQuestionDto> list(ScaleQuestionPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<ScaleQuestion> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(ScaleQuestion::getCreationTime);
        }

        //构建一个分页查询的model
        Page<ScaleQuestion> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取测评题目数据
        IPage<ScaleQuestion> pageRecords= scaleQuestionMapper.selectPage(page, queryWrapper);
      
        //把ScaleQuestion实体转换成ScaleQuestion传输模型
        List<ScaleQuestionDto> items= Extension.copyBeanList(pageRecords.getRecords(),ScaleQuestionDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个测评题目查询
     */
    @SneakyThrows
    @Override
    public ScaleQuestionDto findById(ScaleQuestionPagedInput input) {
       if(input.getId()==null)
        {
         return new ScaleQuestionDto();
        }
      
       PagedResult<ScaleQuestionDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new ScaleQuestionDto()); 
    }

    /**
     *测评题目创建或者修改
     */
    @SneakyThrows
    @Override
    public ScaleQuestionDto addOrEdit(ScaleQuestionDto input) {
        //声明一个测评题目实体
        ScaleQuestion scaleQuestion=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(scaleQuestion);
        //把传输模型返回给前端
        return scaleQuestion.mapToDto();
    }
    /**
     * 测评题目删除
     */
    @Override
    public void deleteById(IdInput input) {
        ScaleQuestion entity = scaleQuestionMapper.selectById(input.getId());
        scaleQuestionMapper.deleteById(entity);
    }

    /**
     * 测评题目批量删除
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
