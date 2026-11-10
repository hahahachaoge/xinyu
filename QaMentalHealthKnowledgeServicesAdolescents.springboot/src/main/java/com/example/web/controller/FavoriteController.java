package com.example.web.controller;

import com.example.web.SysConst;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.service.*;
import com.example.web.common.*;
import com.example.web.exception.CustomException;
import com.example.web.utils.BaseContext;
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
import lombok.Data;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 收藏控制器
 */
@RestController()
@RequestMapping("/favorite")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private FavoriteMapper favoriteMapper;

    /**
     * 收藏分页查询
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<FavoriteDto> list(@RequestBody FavoritePagedInput input) {
        return favoriteService.list(input);
    }

    /**
     * 单个收藏查询接口
     */
    @RequestMapping(value = "/findById", method = RequestMethod.POST)
    @SneakyThrows
    public FavoriteDto findById(@RequestBody FavoritePagedInput input) {

        return favoriteService.findById(input);
    }

    /**
     * 收藏创建或则修改
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public FavoriteDto addOrEdit(@RequestBody FavoriteDto input) throws Exception {
        return favoriteService.addOrEdit(input);
    }

    /**
     * 收藏删除
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public void deleteById(@RequestBody IdInput input) {
        favoriteService.deleteById(input);
    }

    /**
     * 收藏批量删除
     */
    @RequestMapping(value = "/batchDeleteByIds", method = RequestMethod.POST)
    public void batchDeleteByIds(@RequestBody IdsInput input) {
        favoriteService.batchDeleteByIds(input);
    }

    /**
     * 收藏/取消收藏（切换）
     */
    @RequestMapping(value = "/toggle", method = RequestMethod.POST)
    public FavoriteDto toggle(@RequestBody ToggleFavoriteInput input) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        return favoriteService.toggle(userId, input.getFavoriteType(), input.getTargetId());
    }

    /**
     * 取消收藏
     */
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public void cancel(@RequestBody IdInput input) {
        favoriteService.cancel(input);
    }

    /**
     * 检查是否已收藏
     */
    @RequestMapping(value = "/checkFavorited", method = RequestMethod.POST)
    public boolean checkFavorited(@RequestBody ToggleFavoriteInput input) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        return favoriteService.checkFavorited(userId, input.getFavoriteType(), input.getTargetId());
    }

    @Data
    public static class ToggleFavoriteInput {
        private Integer favoriteType;
        private Integer targetId;
    }
}
