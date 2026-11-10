package com.example.web.dto;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.example.web.common.BaseDto;
import com.example.web.entity.SysUser;
import com.example.web.enums.RoleTypeEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SysUserDto extends BaseDto {

    private String userName;// 账号

    private String passWord;// 密码

    private String email;// 邮箱

    private String imageUrls;// 头像

    private String nickName;// 名称

    private String phoneNumber;// 手机号码

    private Integer roleType;// 账号角色

    public String getRoleTypeFormat() {
        return RoleTypeEnum.getEnum(roleType).toString();
    }

    private String orginPassword;// 原密码(修改密码时使用)
    /**
     * 把账号传输模型转换成账号实体
     */
    public SysUser mapToEntity() throws InvocationTargetException, IllegalAccessException {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUser, this);
        return sysUser;
    }

    /**
     * 角色类型格式化
     */
    public String roleTypeFormat() {
        if (roleType == null) {
            return "";
        }
        return RoleTypeEnum.fromIndex(roleType).name();
    }
}
