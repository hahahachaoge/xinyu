package com.example.web.entity;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

import org.apache.commons.beanutils.BeanUtils;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.web.dto.SysUserDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SysUser extends BaseEntity {

    private String userName;// 账号

    private String passWord;// 密码

    private String email;// 邮箱

    private String imageUrls;// 头像

    private String nickName;// 名称

    private String phoneNumber;// 手机号码


    private Integer roleType;// 账号角色
    /**
     * 把账号实体转换成账号传输模型
     */
    public SysUserDto mapToDto() throws InvocationTargetException, IllegalAccessException {
        SysUserDto sysUserDto = new SysUserDto();
        BeanUtils.copyProperties(sysUserDto, this);
        return sysUserDto;
    }
}
