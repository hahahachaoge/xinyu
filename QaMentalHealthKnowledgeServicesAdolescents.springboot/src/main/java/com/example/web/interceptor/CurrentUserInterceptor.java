package com.example.web.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.web.SysConst;
import com.example.web.common.CurrentUserDto;
import com.example.web.enums.RoleTypeEnum;
import com.example.web.utils.BaseContext;
import com.example.web.utils.JWTUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 解析当前token的用户信息 存到线程中
 */
public class CurrentUserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null) {
            token = token.replace("Bearer ", "");
            DecodedJWT jwt = JWTUtils.getTokenInfo(token);
            String userId = jwt.getClaims().get(SysConst.UserIdClaim).asString();
            String roleType = jwt.getClaims().get(SysConst.RoleTypeClaim).asString();
            CurrentUserDto currentUserDto = new CurrentUserDto();
            currentUserDto.setUserId(Integer.valueOf(userId));
            currentUserDto.setRoleType(RoleTypeEnum.getEnum(Integer.valueOf(roleType)));
            BaseContext.setCurrentUserDto(currentUserDto);
        } else {
            CurrentUserDto currentUserDto = new CurrentUserDto();
            currentUserDto.setUserId(0);
            BaseContext.setCurrentUserDto(currentUserDto);
        }
        return true;
    }
}
