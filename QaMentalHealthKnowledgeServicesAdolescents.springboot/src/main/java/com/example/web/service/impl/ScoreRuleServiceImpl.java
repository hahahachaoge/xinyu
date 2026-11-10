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
 * 评分规则功能实现类
 */
@Service
public class ScoreRuleServiceImpl extends ServiceImpl<ScoreRuleMapper, ScoreRule> implements ScoreRuleService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的ScoreRule表mapper对象
     */
    @Autowired
    private ScoreRuleMapper scoreRuleMapper;
    @Autowired
    private ScaleInfoMapper  scaleInfoMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<ScoreRule> builderQuery(ScoreRulePagedInput input) {
       //声明一个支持评分规则查询的(拉姆达)表达式
        LambdaQueryWrapper<ScoreRule> queryWrapper = Wrappers.<ScoreRule>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, ScoreRule::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getResultLabel())) {
             queryWrapper = queryWrapper.like(ScoreRule::getResultLabel, input.getResultLabel());
       	 }

        if (input.getScaleInfoId() != null) {
            queryWrapper = queryWrapper.eq(ScoreRule::getScaleInfoId, input.getScaleInfoId());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(ScoreRule::getResultLabel,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理评分规则对于的外键数据
     */
   private List<ScoreRuleDto> dispatchItem(List<ScoreRuleDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (ScoreRuleDto item : items) {           
          	            
           //查询出关联的ScaleInfo表信息           
            ScaleInfo  scaleInfo= scaleInfoMapper.selectById(item.getScaleInfoId());
            item.setScaleInfoDto(scaleInfo!=null?scaleInfo.mapToDto():new ScaleInfoDto());              
       }
       
     return items; 
   }
  
    /**
     * 评分规则分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<ScoreRuleDto> list(ScoreRulePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<ScoreRule> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(ScoreRule::getCreationTime);
        }

        //构建一个分页查询的model
        Page<ScoreRule> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取评分规则数据
        IPage<ScoreRule> pageRecords= scoreRuleMapper.selectPage(page, queryWrapper);
      
        //把ScoreRule实体转换成ScoreRule传输模型
        List<ScoreRuleDto> items= Extension.copyBeanList(pageRecords.getRecords(),ScoreRuleDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个评分规则查询
     */
    @SneakyThrows
    @Override
    public ScoreRuleDto findById(ScoreRulePagedInput input) {
       if(input.getId()==null)
        {
         return new ScoreRuleDto();
        }
      
       PagedResult<ScoreRuleDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new ScoreRuleDto()); 
    }

    /**
     *评分规则创建或者修改
     */
    @SneakyThrows
    @Override
    public ScoreRuleDto addOrEdit(ScoreRuleDto input) {
        //声明一个评分规则实体
        ScoreRule scoreRule=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(scoreRule);
        //把传输模型返回给前端
        return scoreRule.mapToDto();
    }
    /**
     * 评分规则删除
     */
    @Override
    public void deleteById(IdInput input) {
        ScoreRule entity = scoreRuleMapper.selectById(input.getId());
        scoreRuleMapper.deleteById(entity);
    }

    /**
     * 评分规则批量删除
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
