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
 * 科普文章控制器
 */
@RestController()
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 科普文章分页查询
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<ArticleDto> list(@RequestBody ArticlePagedInput input) {
        return articleService.list(input);
    }

    /**
     * 单个科普文章查询接口
     */
    @RequestMapping(value = "/findById", method = RequestMethod.POST)
    @SneakyThrows
    public ArticleDto findById(@RequestBody ArticlePagedInput input) {

        return articleService.findById(input);
    }

    /**
     * 科普文章创建或则修改
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public ArticleDto addOrEdit(@RequestBody ArticleDto input) throws Exception {
        return articleService.addOrEdit(input);
    }

    /**
     * 科普文章删除
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public void deleteById(@RequestBody IdInput input) {
        articleService.deleteById(input);
    }

    /**
     * 科普文章批量删除
     */
    @RequestMapping(value = "/batchDeleteByIds", method = RequestMethod.POST)
    public void batchDeleteByIds(@RequestBody IdsInput input) {
        articleService.batchDeleteByIds(input);
    }

    /**
     * 浏览文章（增加浏览量）
     */
    @RequestMapping(value = "/view", method = RequestMethod.POST)
    public void view(@RequestBody IdInput input) {
        articleService.view(input);
    }

    /**
     * 协同过滤推荐文章
     */
    @RequestMapping(value = "/recommend", method = RequestMethod.POST)
    @SneakyThrows
    public List<ArticleDto> recommend(@RequestBody Map<String, Integer> input) {
        Integer userId = input.get("userId");
        Integer currentArticleId = input.get("currentArticleId");
        if (userId == null)
            userId = 0;
        return articleService.recommend(userId, currentArticleId);
    }
}
