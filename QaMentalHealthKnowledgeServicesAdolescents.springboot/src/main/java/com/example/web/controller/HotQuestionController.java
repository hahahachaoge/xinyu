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
 * 热门问题控制器 
 */
@RestController()
@RequestMapping("/hotQuestion")
public class HotQuestionController {
    @Autowired
    private  HotQuestionService hotQuestionService;
    @Autowired
    private HotQuestionMapper hotQuestionMapper;
    /**
     * 热门问题分页查询
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<HotQuestionDto> list(@RequestBody HotQuestionPagedInput input)  {
        return hotQuestionService.list(input);
    }
     /**
     * 单个热门问题查询接口
     */
    @RequestMapping(value = "/findById", method = RequestMethod.POST)
    @SneakyThrows
    public HotQuestionDto findById(@RequestBody HotQuestionPagedInput input) {

        return hotQuestionService.findById(input);
    }
  
    /**
     * 热门问题创建或则修改
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public HotQuestionDto addOrEdit(@RequestBody HotQuestionDto input) throws Exception {
        return hotQuestionService.addOrEdit(input);
    }
    /**
     * 热门问题删除
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public void deleteById(@RequestBody IdInput input)
    {
        hotQuestionService.deleteById(input);
    }

    /**
     * 热门问题批量删除
     */
    @RequestMapping(value = "/batchDeleteByIds", method = RequestMethod.POST)
    public void batchDeleteByIds(@RequestBody IdsInput input)
    {
        hotQuestionService.batchDeleteByIds(input);
    }
  

 
}
