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
 * 文章分类功能实现类
 */
@Service
public class ArticleTypeServiceImpl extends ServiceImpl<ArticleTypeMapper, ArticleType> implements ArticleTypeService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的ArticleType表mapper对象
     */
    @Autowired
    private ArticleTypeMapper articleTypeMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<ArticleType> builderQuery(ArticleTypePagedInput input) {
       //声明一个支持文章分类查询的(拉姆达)表达式
        LambdaQueryWrapper<ArticleType> queryWrapper = Wrappers.<ArticleType>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, ArticleType::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getTypeName())) {
             queryWrapper = queryWrapper.like(ArticleType::getTypeName, input.getTypeName());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(ArticleType::getTypeName,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理文章分类对于的外键数据
     */
   private List<ArticleTypeDto> dispatchItem(List<ArticleTypeDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (ArticleTypeDto item : items) {       }
       
     return items; 
   }
  
    /**
     * 文章分类分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<ArticleTypeDto> list(ArticleTypePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<ArticleType> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(ArticleType::getCreationTime);
        }

        //构建一个分页查询的model
        Page<ArticleType> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取文章分类数据
        IPage<ArticleType> pageRecords= articleTypeMapper.selectPage(page, queryWrapper);
      
        //把ArticleType实体转换成ArticleType传输模型
        List<ArticleTypeDto> items= Extension.copyBeanList(pageRecords.getRecords(),ArticleTypeDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个文章分类查询
     */
    @SneakyThrows
    @Override
    public ArticleTypeDto findById(ArticleTypePagedInput input) {
       if(input.getId()==null)
        {
         return new ArticleTypeDto();
        }
      
       PagedResult<ArticleTypeDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new ArticleTypeDto()); 
    }

    /**
     *文章分类创建或者修改
     */
    @SneakyThrows
    @Override
    public ArticleTypeDto addOrEdit(ArticleTypeDto input) {
        //声明一个文章分类实体
        ArticleType articleType=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(articleType);
        //把传输模型返回给前端
        return articleType.mapToDto();
    }
    /**
     * 文章分类删除
     */
    @Override
    public void deleteById(IdInput input) {
        ArticleType entity = articleTypeMapper.selectById(input.getId());
        articleTypeMapper.deleteById(entity);
    }

    /**
     * 文章分类批量删除
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
