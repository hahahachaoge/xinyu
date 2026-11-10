package com.example.web.controller;
import com.example.web.SysConst;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.service.*;
import com.example.web.common.*;
import com.example.web.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.SneakyThrows;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletResponse;
/**
 * 问答反馈控制器 
 */
@RestController()
@RequestMapping("/qaFeedback")
public class QaFeedbackController {
    @Autowired
    private  QaFeedbackService qaFeedbackService;
    @Autowired
    private QaFeedbackMapper qaFeedbackMapper;
    /**
     * 问答反馈分页查询
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<QaFeedbackDto> list(@RequestBody QaFeedbackPagedInput input)  {
        return qaFeedbackService.list(input);
    }
     /**
     * 单个问答反馈查询接口
     */
    @RequestMapping(value = "/findById", method = RequestMethod.POST)
    @SneakyThrows
    public QaFeedbackDto findById(@RequestBody QaFeedbackPagedInput input) {

        return qaFeedbackService.findById(input);
    }
  
    /**
     * 问答反馈创建或则修改
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public QaFeedbackDto addOrEdit(@RequestBody QaFeedbackDto input) throws Exception {
        return qaFeedbackService.addOrEdit(input);
    }
    /**
     * 问答反馈删除
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public void deleteById(@RequestBody IdInput input)
    {
        qaFeedbackService.deleteById(input);
    }

    /**
     * 问答反馈批量删除
     */
    @RequestMapping(value = "/batchDeleteByIds", method = RequestMethod.POST)
    public void batchDeleteByIds(@RequestBody IdsInput input)
    {
        qaFeedbackService.batchDeleteByIds(input);
    }
  

 
}
