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
 * 收藏功能的Service接口的定义清单
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 收藏的分页查询方法接口定义
     */
    public PagedResult<FavoriteDto> list(FavoritePagedInput input);

    /**
     * 收藏的新增或者修改方法接口定义
     */
    public FavoriteDto addOrEdit(FavoriteDto input);

    /**
     * 获取收藏信息
     */
    public FavoriteDto findById(FavoritePagedInput input);

    /**
     * 收藏删除
     */
    public void deleteById(IdInput input);

    /**
     * 收藏批量删除
     */
    public void batchDeleteByIds(IdsInput input);

    /**
     * 收藏/取消收藏（切换）
     */
    public FavoriteDto toggle(Integer userId, Integer favoriteType, Integer targetId);

    /**
     * 取消收藏
     */
    public void cancel(IdInput input);

    /**
     * 检查是否已收藏
     */
    public boolean checkFavorited(Integer userId, Integer favoriteType, Integer targetId);

}
