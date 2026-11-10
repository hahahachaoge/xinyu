package com.example.web.utils;

import org.springframework.context.ApplicationContext;

import com.example.web.common.CurrentUserDto;

/**
 * 当前线程中的一些公共上下文
 */
public class BaseContext {
    private static ThreadLocal<CurrentUserDto> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前的用户信息
     */
    public static void setCurrentUserDto(CurrentUserDto dto) {
        threadLocal.set(dto);
    }

    /**
     * 获取当前的用户信息
     */
    public static CurrentUserDto getCurrentUserDto() {
        return threadLocal.get();
    }

    public static void clear() { threadLocal.remove(); }

    private static ApplicationContext _applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        _applicationContext = applicationContext;
    }

    /**
     * 得到应用程序的上下文对象
     */
    public static ApplicationContext getApplicationContext() {
        return _applicationContext;
    }
}
