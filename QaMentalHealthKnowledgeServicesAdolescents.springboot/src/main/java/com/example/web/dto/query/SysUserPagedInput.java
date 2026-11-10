package com.example.web.dto.query;

import com.example.web.common.PagedInput;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SysUserPagedInput extends PagedInput {
    private Integer id;

    /**
     * 姓名
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 账号
     */
    private String userName;

    /**
     * 账号角色
     */
    private Integer roleType;
}
