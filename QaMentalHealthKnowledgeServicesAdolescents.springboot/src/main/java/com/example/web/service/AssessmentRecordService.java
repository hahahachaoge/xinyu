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
 * 测评记录功能的Service接口的定义清单
 */
public interface AssessmentRecordService extends IService<AssessmentRecord> {

    /**
     * 测评记录的分页查询方法接口定义
     */
    public PagedResult<AssessmentRecordDto> list(AssessmentRecordPagedInput input) ;
    /**
     * 测评记录的新增或者修改方法接口定义
     */
    public AssessmentRecordDto addOrEdit(AssessmentRecordDto input);

     /**
     * 获取测评记录信息
     */
    public AssessmentRecordDto findById(AssessmentRecordPagedInput input);
 	 /**
     * 测评记录删除
     */
    public void deleteById(IdInput input);

    /**
     * 测评记录批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
