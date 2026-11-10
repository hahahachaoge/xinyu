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
 * 评分规则控制器 
 */
@RestController()
@RequestMapping("/scoreRule")
public class ScoreRuleController {
    @Autowired
    private  ScoreRuleService scoreRuleService;
    @Autowired
    private ScoreRuleMapper scoreRuleMapper;
    /**
     * 评分规则分页查询
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<ScoreRuleDto> list(@RequestBody ScoreRulePagedInput input)  {
        return scoreRuleService.list(input);
    }
     /**
     * 单个评分规则查询接口
     */
    @RequestMapping(value = "/findById", method = RequestMethod.POST)
    @SneakyThrows
    public ScoreRuleDto findById(@RequestBody ScoreRulePagedInput input) {

        return scoreRuleService.findById(input);
    }
  
    /**
     * 评分规则创建或则修改
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public ScoreRuleDto addOrEdit(@RequestBody ScoreRuleDto input) throws Exception {
        return scoreRuleService.addOrEdit(input);
    }
    /**
     * 评分规则删除
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public void deleteById(@RequestBody IdInput input)
    {
        scoreRuleService.deleteById(input);
    }

    /**
     * 评分规则批量删除
     */
    @RequestMapping(value = "/batchDeleteByIds", method = RequestMethod.POST)
    public void batchDeleteByIds(@RequestBody IdsInput input)
    {
        scoreRuleService.batchDeleteByIds(input);
    }
  

 
}
