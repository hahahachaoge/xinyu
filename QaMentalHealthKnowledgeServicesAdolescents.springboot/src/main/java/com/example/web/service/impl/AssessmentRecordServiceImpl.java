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
 * 测评记录功能实现类
 */
@Service
public class AssessmentRecordServiceImpl extends ServiceImpl<AssessmentRecordMapper, AssessmentRecord> implements AssessmentRecordService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的AssessmentRecord表mapper对象
     */
    @Autowired
    private AssessmentRecordMapper assessmentRecordMapper;
    @Autowired
    private ScaleInfoMapper  scaleInfoMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<AssessmentRecord> builderQuery(AssessmentRecordPagedInput input) {
       //声明一个支持测评记录查询的(拉姆达)表达式
        LambdaQueryWrapper<AssessmentRecord> queryWrapper = Wrappers.<AssessmentRecord>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, AssessmentRecord::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getResultLabel())) {
             queryWrapper = queryWrapper.like(AssessmentRecord::getResultLabel, input.getResultLabel());
       	 }

        if (input.getUserId() != null) {
            queryWrapper = queryWrapper.eq(AssessmentRecord::getUserId, input.getUserId());
       	 }

        if (input.getScaleInfoId() != null) {
            queryWrapper = queryWrapper.eq(AssessmentRecord::getScaleInfoId, input.getScaleInfoId());
       	 }
        if (input.getFinishTimeRange() != null && !input.getFinishTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(AssessmentRecord::getFinishTime, input.getFinishTimeRange().get(1));
            queryWrapper = queryWrapper.ge(AssessmentRecord::getFinishTime, input.getFinishTimeRange().get(0));
        }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(AssessmentRecord::getResultLabel,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理测评记录对于的外键数据
     */
   private List<AssessmentRecordDto> dispatchItem(List<AssessmentRecordDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (AssessmentRecordDto item : items) {           
          	            
           //查询出关联的ScaleInfo表信息           
            ScaleInfo  scaleInfo= scaleInfoMapper.selectById(item.getScaleInfoId());
            item.setScaleInfoDto(scaleInfo!=null?scaleInfo.mapToDto():new ScaleInfoDto());              
           
          	            
           //查询出关联的SysUser表信息           
            SysUser  user= sysUserMapper.selectById(item.getUserId());
            item.setUserDto(user!=null?user.mapToDto():new SysUserDto());              
       }
       
     return items; 
   }
  
    /**
     * 测评记录分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<AssessmentRecordDto> list(AssessmentRecordPagedInput input) {
        if (!AccessControl.isAdmin()) {
            if (input.getUserId() != null) AccessControl.requireOwner(input.getUserId());
            input.setUserId(AccessControl.requireUser().getUserId());
        }
			//构建where条件+排序
        LambdaQueryWrapper<AssessmentRecord> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(AssessmentRecord::getCreationTime);
        }

        //构建一个分页查询的model
        Page<AssessmentRecord> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取测评记录数据
        IPage<AssessmentRecord> pageRecords= assessmentRecordMapper.selectPage(page, queryWrapper);
      
        //把AssessmentRecord实体转换成AssessmentRecord传输模型
        List<AssessmentRecordDto> items= Extension.copyBeanList(pageRecords.getRecords(),AssessmentRecordDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个测评记录查询
     */
    @SneakyThrows
    @Override
    public AssessmentRecordDto findById(AssessmentRecordPagedInput input) {
       if(input.getId()==null)
        {
         return new AssessmentRecordDto();
        }
      
       PagedResult<AssessmentRecordDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new AssessmentRecordDto()); 
    }

    /**
     *测评记录创建或者修改
     */
    @SneakyThrows
    @Override
    public AssessmentRecordDto addOrEdit(AssessmentRecordDto input) {
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED, "请使用测评提交接口，测评结果不可直接修改");
    }

    /**
     * 测评记录删除
     */
    @Override
    public void deleteById(IdInput input) {
        AssessmentRecord entity = assessmentRecordMapper.selectById(input.getId());
        if (entity == null) return;
        AccessControl.requireOwner(entity.getUserId());
        assessmentRecordMapper.deleteById(entity);
    }

    /**
     * 测评记录批量删除
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public void batchDeleteByIds(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            deleteById(idInput);
        }
    }
}
