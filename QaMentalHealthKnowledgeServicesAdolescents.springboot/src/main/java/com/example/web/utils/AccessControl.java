package com.example.web.utils;

import com.example.web.common.CurrentUserDto;
import com.example.web.enums.RoleTypeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class AccessControl {
    private AccessControl() { }
    public static CurrentUserDto requireUser() {
        CurrentUserDto user = BaseContext.getCurrentUserDto();
        if (user == null || user.getUserId() == null || user.getUserId() <= 0)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        return user;
    }
    public static boolean isAdmin() { return requireUser().getRoleType() == RoleTypeEnum.管理员; }
    public static void requireAdmin() {
        if (!isAdmin()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要管理员权限");
    }
    public static void requireOwner(Integer ownerId) {
        CurrentUserDto user = requireUser();
        if (!user.getUserId().equals(ownerId) && user.getRoleType() != RoleTypeEnum.管理员)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问该记录");
    }
}
