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
 * 测评题目功能的Service接口的定义清单
 */
public interface ScaleQuestionService extends IService<ScaleQuestion> {

    /**
     * 测评题目的分页查询方法接口定义
     */
    public PagedResult<ScaleQuestionDto> list(ScaleQuestionPagedInput input) ;
    /**
     * 测评题目的新增或者修改方法接口定义
     */
    public ScaleQuestionDto addOrEdit(ScaleQuestionDto input);

     /**
     * 获取测评题目信息
     */
    public ScaleQuestionDto findById(ScaleQuestionPagedInput input);
 	 /**
     * 测评题目删除
     */
    public void deleteById(IdInput input);

    /**
     * 测评题目批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
