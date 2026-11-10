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
 * 问答反馈功能的Service接口的定义清单
 */
public interface QaFeedbackService extends IService<QaFeedback> {

    /**
     * 问答反馈的分页查询方法接口定义
     */
    public PagedResult<QaFeedbackDto> list(QaFeedbackPagedInput input) ;
    /**
     * 问答反馈的新增或者修改方法接口定义
     */
    public QaFeedbackDto addOrEdit(QaFeedbackDto input);

     /**
     * 获取问答反馈信息
     */
    public QaFeedbackDto findById(QaFeedbackPagedInput input);
 	 /**
     * 问答反馈删除
     */
    public void deleteById(IdInput input);

    /**
     * 问答反馈批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
