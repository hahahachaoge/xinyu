package com.example.web.common;

import com.example.web.enums.RoleTypeEnum;

import lombok.Data;

@Data
public class CurrentUserDto {

    private Integer userId;

    private RoleTypeEnum roleType;
}
