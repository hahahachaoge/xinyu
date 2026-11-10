package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.SysConst;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.enums.*;
import com.example.web.service.*;
import com.example.web.common.*;
import com.example.web.exception.CustomException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import lombok.SneakyThrows;
import java.io.IOException;
import com.example.web.utils.*;
import java.text.DecimalFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 收藏功能实现类
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 操作数据库的Favorite表mapper对象
     */
    @Autowired
    private FavoriteMapper favoriteMapper;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<Favorite> builderQuery(FavoritePagedInput input) {
        // 声明一个支持收藏查询的(拉姆达)表达式
        LambdaQueryWrapper<Favorite> queryWrapper = Wrappers.<Favorite>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, Favorite::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件

        if (input.getUserId() != null) {
            queryWrapper = queryWrapper.eq(Favorite::getUserId, input.getUserId());
        }

        if (input.getFavoriteType() != null) {
            queryWrapper = queryWrapper.eq(Favorite::getFavoriteType, input.getFavoriteType());
        }

        return queryWrapper;
    }

    /**
     * 处理收藏对于的外键数据
     */
    private List<FavoriteDto> dispatchItem(List<FavoriteDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (FavoriteDto item : items) {

            // 查询出关联的SysUser表信息
            SysUser user = sysUserMapper.selectById(item.getUserId());
            item.setUserDto(user != null ? user.mapToDto() : new SysUserDto());
        }

        return items;
    }

    /**
     * 收藏分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<FavoriteDto> list(FavoritePagedInput input) {
        if (!AccessControl.isAdmin()) {
            if (input.getUserId() != null) AccessControl.requireOwner(input.getUserId());
            input.setUserId(AccessControl.requireUser().getUserId());
        }
        // 构建where条件+排序
        LambdaQueryWrapper<Favorite> queryWrapper = builderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(Favorite::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<Favorite> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取收藏数据
        IPage<Favorite> pageRecords = favoriteMapper.selectPage(page, queryWrapper);

        // 把Favorite实体转换成Favorite传输模型
        List<FavoriteDto> items = Extension.copyBeanList(pageRecords.getRecords(), FavoriteDto.class);

        dispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());

    }

    /**
     * 单个收藏查询
     */
    @SneakyThrows
    @Override
    public FavoriteDto findById(FavoritePagedInput input) {
        if (input.getId() == null) {
            return new FavoriteDto();
        }

        PagedResult<FavoriteDto> pagedResult = list(input);
        return pagedResult.getItems().stream().findFirst().orElse(new FavoriteDto());
    }

    /**
     * 收藏创建或者修改
     */
    @SneakyThrows
    @Override
    public FavoriteDto addOrEdit(FavoriteDto input) {
        // 声明一个收藏实体
        Favorite favorite = input.mapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(favorite);
        // 把传输模型返回给前端
        return favorite.mapToDto();
    }

    /**
     * 收藏删除
     */
    @Override
    public void deleteById(IdInput input) {
        Favorite entity = favoriteMapper.selectById(input.getId());
        if (entity == null) return;
        AccessControl.requireOwner(entity.getUserId());
        favoriteMapper.deleteById(entity);
    }

    /**
     * 收藏批量删除
     */
    @Override
    public void batchDeleteByIds(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            deleteById(idInput);
        }
    }

    /**
     * 收藏/取消收藏（切换）
     */
    @SneakyThrows
    @Override
    public FavoriteDto toggle(Integer userId, Integer favoriteType, Integer targetId) {
        Favorite existing = favoriteMapper.selectOne(
                Wrappers.<Favorite>lambdaQuery()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getFavoriteType, favoriteType)
                        .eq(Favorite::getTargetId, targetId));
        if (existing != null) {
            // 已收藏则取消
            favoriteMapper.deleteById(existing.getId());
            return null;
        } else {
            // 未收藏则添加
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setFavoriteType(favoriteType);
            favorite.setTargetId(targetId);
            favorite.setCreationTime(java.time.LocalDateTime.now());
            favoriteMapper.insert(favorite);
            return favorite.mapToDto();
        }
    }

    /**
     * 取消收藏
     */
    @SneakyThrows
    @Override
    public void cancel(IdInput input) {
        Favorite entity = favoriteMapper.selectById(input.getId());
        if (entity == null) {
            throw new CustomException("收藏记录不存在");
        }
        AccessControl.requireOwner(entity.getUserId());
        favoriteMapper.deleteById(entity.getId());
    }

    /**
     * 检查是否已收藏
     */
    @Override
    public boolean checkFavorited(Integer userId, Integer favoriteType, Integer targetId) {
        Long count = favoriteMapper.selectCount(
                Wrappers.<Favorite>lambdaQuery()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getFavoriteType, favoriteType)
                        .eq(Favorite::getTargetId, targetId));
        return count > 0;
    }
}
