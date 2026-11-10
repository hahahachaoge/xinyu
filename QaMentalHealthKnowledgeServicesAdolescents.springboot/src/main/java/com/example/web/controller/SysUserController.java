package com.example.web.controller;

import java.io.IOException;

import com.example.web.exception.CustomException;
import com.example.web.utils.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web.common.IdInput;
import com.example.web.common.IdsInput;
import com.example.web.common.PagedResult;
import com.example.web.common.ResponseData;
import com.example.web.dto.SysUserDto;
import com.example.web.dto.query.SysUserPagedInput;
import com.example.web.service.SysUserService;
import com.example.web.utils.BaseContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;


/**
 * 账号控制器
 */
@RestController()
@RequestMapping("/sysUser")
public class SysUserController {
    @Autowired()
    private SysUserService sysUserService;

    /**
     * 账号分页查询接口
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<SysUserDto> list(@RequestBody SysUserPagedInput input) {

        return sysUserService.list(input);
    }

    /**
     * 账号创建或则修改接口
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public SysUserDto addOrEdit(@RequestBody SysUserDto input) {
        return sysUserService.addOrEdit(input);

    }

    /**
     * 账号删除
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public void deleteById(@RequestBody IdInput input) {
        sysUserService.deleteById(input);
    }

    /**
     * 账号批量删除
     */
    @RequestMapping(value = "/batchDeleteByIds", method = RequestMethod.POST)
    public void batchDeleteByIds(@RequestBody IdsInput input) {
        sysUserService.batchDeleteByIds(input);
    }

    /**
     * 查询单个对账号
     */
    @RequestMapping(value = "/findById", method = RequestMethod.POST)
    public SysUserDto findById(@RequestBody SysUserPagedInput input) {
        return sysUserService.findById(input);
    }

    /**
     * 账号登录
     */
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public ResponseData<String> signIn(@RequestBody SysUserDto input, HttpServletRequest request) {
      String token = sysUserService.signIn(input);
        return ResponseData.getResponseDataInstance(token, "登录成功", true);
    }

    /**
     * 获取账号信息
     */
    @SneakyThrows
    @RequestMapping(value = "/getByToken", method = RequestMethod.POST)
    public SysUserDto getByToken(@RequestHeader("Authorization") String token) {

        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        SysUserPagedInput queryInput = new SysUserPagedInput();
        queryInput.setId(userId);
        SysUserDto sysUserDto = sysUserService.findById(queryInput);

        return sysUserDto;
    }

    /**
     * 账号注册接口
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public SysUserDto register(@RequestBody SysUserDto input) throws Exception {

        return sysUserService.register(input);

    }

    /**
     * 找回密码
     */
    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public void forgetPassword(@RequestBody SysUserDto input) throws Exception {
        sysUserService.forgetPassword(input);
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public void changePassword(@RequestBody SysUserDto input) throws Exception {
        sysUserService.changePassword(input);
    }

    /**
     * 重置密码
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public void resetPassword(@RequestBody SysUserDto input) throws Exception {
        sysUserService.resetPassword(input);
    }

    /**
     * 账号导出
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(@RequestParam String query, HttpServletResponse response) throws IOException {
        sysUserService.export(query, response);
    }

}
