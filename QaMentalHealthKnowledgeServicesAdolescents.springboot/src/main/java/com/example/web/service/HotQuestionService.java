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
 * 热门问题功能的Service接口的定义清单
 */
public interface HotQuestionService extends IService<HotQuestion> {

    /**
     * 热门问题的分页查询方法接口定义
     */
    public PagedResult<HotQuestionDto> list(HotQuestionPagedInput input) ;
    /**
     * 热门问题的新增或者修改方法接口定义
     */
    public HotQuestionDto addOrEdit(HotQuestionDto input);

     /**
     * 获取热门问题信息
     */
    public HotQuestionDto findById(HotQuestionPagedInput input);
 	 /**
     * 热门问题删除
     */
    public void deleteById(IdInput input);

    /**
     * 热门问题批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
