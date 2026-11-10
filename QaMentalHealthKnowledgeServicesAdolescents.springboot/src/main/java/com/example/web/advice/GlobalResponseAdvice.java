package com.example.web.advice;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.web.common.ResponseData;

/**
 * 全局响应处理切面
 */
@ControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Value("${server.port:7406}")
    private String serverPort;

    @Value("${server.ip:http://localhost:7406}")
    private String serverIp;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        Object result;
        if (body == null) {
            result = ResponseData.ofSuccess();
        } else if (body instanceof ResponseData<?>) {
            result = body;
        } else {
            result = ResponseData.getResponseDataInstance(body, "成功", true);
        }

        if (result instanceof ResponseData<?>) {
            try {
                ResponseData<?> responseData = (ResponseData<?>) result;
                Object data = responseData.getData();
                if (data != null) {
                    replaceUrlInObject(data, new HashSet<>());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private void replaceUrlInObject(Object obj, Set<Object> visited) {
        if (obj == null || visited.contains(obj)) {
            return;
        }

        if (!isSimpleType(obj.getClass())) {
            visited.add(obj);
        }

        String oldUrl = "http://localhost:" + serverPort + "/";

        if (obj instanceof Collection<?>) {
            for (Object item : (Collection<?>) obj) {
                replaceUrlInObject(item, visited);
            }
            return;
        }

        if (obj instanceof Map<?, ?>) {
            for (Object value : ((Map<?, ?>) obj).values()) {
                replaceUrlInObject(value, visited);
            }
            return;
        }

        if (obj.getClass().isArray()) {
            if (obj instanceof Object[]) {
                for (Object item : (Object[]) obj) {
                    replaceUrlInObject(item, visited);
                }
            }
            return;
        }

        if (isSimpleType(obj.getClass())) {
            return;
        }

        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(obj);

                    if (fieldValue == null) {
                        continue;
                    }

                    if (fieldValue instanceof String) {
                        String strValue = (String) fieldValue;
                        if (strValue.contains(oldUrl)) {
                            String newValue = strValue.replace(oldUrl, serverIp + "/");
                            field.set(obj, newValue);
                        }
                    } else {
                        replaceUrlInObject(fieldValue, visited);
                    }
                } catch (Exception e) {
                    // 忽略无法访问的字段
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || Number.class.isAssignableFrom(clazz)
                || clazz == Boolean.class
                || clazz == Character.class
                || clazz.isEnum();
    }
}
