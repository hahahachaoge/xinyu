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
 * 轮播图功能实现类
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的Banner表mapper对象
     */
    @Autowired
    private BannerMapper bannerMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<Banner> builderQuery(BannerPagedInput input) {
       //声明一个支持轮播图查询的(拉姆达)表达式
        LambdaQueryWrapper<Banner> queryWrapper = Wrappers.<Banner>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, Banner::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getTitle())) {
             queryWrapper = queryWrapper.like(Banner::getTitle, input.getTitle());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(Banner::getTitle,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
    /**
     * 处理轮播图对于的外键数据
     */
   private List<BannerDto> dispatchItem(List<BannerDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (BannerDto item : items) {       }
       
     return items; 
   }
  
    /**
     * 轮播图分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<BannerDto> list(BannerPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<Banner> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(Banner::getCreationTime);
        }

        //构建一个分页查询的model
        Page<Banner> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取轮播图数据
        IPage<Banner> pageRecords= bannerMapper.selectPage(page, queryWrapper);
      
        //把Banner实体转换成Banner传输模型
        List<BannerDto> items= Extension.copyBeanList(pageRecords.getRecords(),BannerDto.class);

		  dispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }
  
    /**
     * 单个轮播图查询
     */
    @SneakyThrows
    @Override
    public BannerDto findById(BannerPagedInput input) {
       if(input.getId()==null)
        {
         return new BannerDto();
        }
      
       PagedResult<BannerDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new BannerDto()); 
    }

    /**
     *轮播图创建或者修改
     */
    @SneakyThrows
    @Override
    public BannerDto addOrEdit(BannerDto input) {
        //声明一个轮播图实体
        Banner banner=input.mapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(banner);
        //把传输模型返回给前端
        return banner.mapToDto();
    }
    /**
     * 轮播图删除
     */
    @Override
    public void deleteById(IdInput input) {
        Banner entity = bannerMapper.selectById(input.getId());
        bannerMapper.deleteById(entity);
    }

    /**
     * 轮播图批量删除
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
