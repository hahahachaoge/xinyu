package com.example.web.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.common.*;
import com.example.web.enums.*;
import java.lang.reflect.InvocationTargetException;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletResponse;
/**
 * 轮播图功能的Service接口的定义清单
 */
public interface BannerService extends IService<Banner> {

    /**
     * 轮播图的分页查询方法接口定义
     */
    public PagedResult<BannerDto> list(BannerPagedInput input) ;
    /**
     * 轮播图的新增或者修改方法接口定义
     */
    public BannerDto addOrEdit(BannerDto input);

     /**
     * 获取轮播图信息
     */
    public BannerDto findById(BannerPagedInput input);
 	 /**
     * 轮播图删除
     */
    public void deleteById(IdInput input);

    /**
     * 轮播图批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
