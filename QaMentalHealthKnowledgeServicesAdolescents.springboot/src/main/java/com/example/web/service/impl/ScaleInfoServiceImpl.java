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
 * 测评量功能实现类
 */
@Service
public class ScaleInfoServiceImpl extends ServiceImpl<ScaleInfoMapper, ScaleInfo> implements ScaleInfoService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的ScaleInfo表mapper对象
     */
    @Autowired
    private ScaleInfoMapper scaleInfoMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<ScaleInfo> builderQuery(ScaleInfoPagedInput input) {
       //声明一个支持测评量查询的(拉姆达)表达式
        LambdaQueryWrapper<ScaleInfo> queryWrapper = Wrappers.<ScaleInfo>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, ScaleInfo::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getScaleName())) {
             queryWrapper = queryWrapper.like(ScaleInfo::getScaleName, input.getScaleName());
       	 }
        if (Extension.isNotNullOrEmpty(input.getTargetGroup())) {
             queryWrapper = queryWrapper.like(ScaleInfo::getTargetGroup, input.getTargetGroup());
       	 }

        if (input.getScaleType() != null) {
            queryWrapper = queryWrapper.eq(ScaleInfo::getScaleType, input.getScaleType());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(ScaleInfo::getScaleName,input.getKeyWord()).or()   	 
          	   .like(ScaleInfo::getTargetGroup,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理测评量对于的外键数据
     */
   private List<ScaleInfoDto> dispatchItem(List<ScaleInfoDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (ScaleInfoDto item : items) {       }
       
     return items; 
   }
  
    /**
     * 测评量分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<ScaleInfoDto> list(ScaleInfoPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<ScaleInfo> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(ScaleInfo::getCreationTime);
        }

        //构建一个分页查询的model
        Page<ScaleInfo> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取测评量数据
        IPage<ScaleInfo> pageRecords= scaleInfoMapper.selectPage(page, queryWrapper);
      
        //把ScaleInfo实体转换成ScaleInfo传输模型
        List<ScaleInfoDto> items= Extension.copyBeanList(pageRecords.getRecords(),ScaleInfoDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个测评量查询
     */
    @SneakyThrows
    @Override
    public ScaleInfoDto findById(ScaleInfoPagedInput input) {
       if(input.getId()==null)
        {
         return new ScaleInfoDto();
        }
      
       PagedResult<ScaleInfoDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new ScaleInfoDto()); 
    }

    /**
     *测评量创建或者修改
     */
    @SneakyThrows
    @Override
    public ScaleInfoDto addOrEdit(ScaleInfoDto input) {
        //声明一个测评量实体
        ScaleInfo scaleInfo=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(scaleInfo);
        //把传输模型返回给前端
        return scaleInfo.mapToDto();
    }
    /**
     * 测评量删除
     */
    @Override
    public void deleteById(IdInput input) {
        ScaleInfo entity = scaleInfoMapper.selectById(input.getId());
        scaleInfoMapper.deleteById(entity);
    }

    /**
     * 测评量批量删除
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
