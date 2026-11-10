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
 * 对话会话控制器 
 */
@RestController()
@RequestMapping("/chatSession")
public class ChatSessionController {
    @Autowired
    private  ChatSessionService chatSessionService;
    @Autowired
    private ChatSessionMapper chatSessionMapper;
    /**
     * 对话会话分页查询
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<ChatSessionDto> list(@RequestBody ChatSessionPagedInput input)  {
        return chatSessionService.list(input);
    }
     /**
     * 单个对话会话查询接口
     */
    @RequestMapping(value = "/findById", method = RequestMethod.POST)
    @SneakyThrows
    public ChatSessionDto findById(@RequestBody ChatSessionPagedInput input) {

        return chatSessionService.findById(input);
    }
  
    /**
     * 对话会话创建或则修改
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public ChatSessionDto addOrEdit(@RequestBody ChatSessionDto input) throws Exception {
        return chatSessionService.addOrEdit(input);
    }
    /**
     * 对话会话删除
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public void deleteById(@RequestBody IdInput input)
    {
        chatSessionService.deleteById(input);
    }

    /**
     * 对话会话批量删除
     */
    @RequestMapping(value = "/batchDeleteByIds", method = RequestMethod.POST)
    public void batchDeleteByIds(@RequestBody IdsInput input)
    {
        chatSessionService.batchDeleteByIds(input);
    }
  

 
}
