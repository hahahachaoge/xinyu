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
 * 测评量功能的Service接口的定义清单
 */
public interface ScaleInfoService extends IService<ScaleInfo> {

    /**
     * 测评量的分页查询方法接口定义
     */
    public PagedResult<ScaleInfoDto> list(ScaleInfoPagedInput input) ;
    /**
     * 测评量的新增或者修改方法接口定义
     */
    public ScaleInfoDto addOrEdit(ScaleInfoDto input);

     /**
     * 获取测评量信息
     */
    public ScaleInfoDto findById(ScaleInfoPagedInput input);
 	 /**
     * 测评量删除
     */
    public void deleteById(IdInput input);

    /**
     * 测评量批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
