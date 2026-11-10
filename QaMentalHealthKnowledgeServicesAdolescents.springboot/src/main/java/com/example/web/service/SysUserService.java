package com.example.web.service;

import java.io.IOException;

import org.springframework.web.bind.annotation.RequestParam;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.web.common.IdInput;
import com.example.web.common.IdsInput;
import com.example.web.common.PagedResult;
import com.example.web.dto.SysUserDto;
import com.example.web.dto.query.SysUserPagedInput;
import com.example.web.entity.SysUser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 账号功能的Service接口的定义清单
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 账号的分页查询方法接口定义
     */
    public PagedResult<SysUserDto> list(SysUserPagedInput input);

    /**
     * 账号的新增或者修改方法接口定义
     */
    public SysUserDto addOrEdit(SysUserDto input);

    /**
     * 账号模块删除
     */
    public void deleteById(IdInput input);

    /**
     * 账号模块批量删除
     */
    public void batchDeleteByIds(IdsInput input);

    /**
     * 查询单个对象
     */
    public SysUserDto findById(SysUserPagedInput input);

    /**
     * 账号登录
     */
    public String signIn(SysUserDto input);

    /**
     * 注册
     */
    public SysUserDto register(SysUserDto input);

    /**
     * 找回密码
     */
    void forgetPassword(SysUserDto input);

    /**
     * 修改密码
     */
    void changePassword(SysUserDto input);

    /**
     * 重置密码
     */
    void resetPassword(SysUserDto input);

    /**
     * 账号导出
     */
    public void export(@RequestParam String query, HttpServletResponse response) throws IOException;
}
