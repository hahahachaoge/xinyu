package com.example.web.advice;

import java.time.LocalDateTime;

import com.example.web.common.CurrentUserDto;
import com.example.web.utils.BaseContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

/**
 * mybatis 插入数据之前进行的元数据处理
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 当前账号信息
     */
    private CurrentUserDto currentUserDto;

    public MyMetaObjectHandler() {

    }

    /**
     * 数据库执行insert时候的 处理
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        // 创建的时候给表创建时间赋值
        if (this.getFieldValByName("creationTime", metaObject) == null) {
            this.setFieldValByName("creationTime", LocalDateTime.now(), metaObject);
        }
        // 获取当前请求管道中的账号信息
        currentUserDto = BaseContext.getCurrentUserDto();

    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
