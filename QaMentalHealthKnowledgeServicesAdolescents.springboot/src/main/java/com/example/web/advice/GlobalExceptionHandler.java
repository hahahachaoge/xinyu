package com.example.web.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.web.SysConst;
import com.example.web.common.ResponseData;
import com.example.web.exception.CustomException;

/**
 * 全局异常处理切面
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseBody
    public org.springframework.http.ResponseEntity<ResponseData<?>> malformedRequest() {
        ResponseData<Object> body = new ResponseData<>();
        body.setCode("400");
        body.setSuccess(false);
        body.setMsg("请求格式或字段值无效");
        return org.springframework.http.ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    @ResponseBody
    public org.springframework.http.ResponseEntity<ResponseData<?>> accessException(
            org.springframework.web.server.ResponseStatusException e) {
        ResponseData<Object> body = new ResponseData<>();
        body.setCode(String.valueOf(e.getStatusCode().value()));
        body.setSuccess(false);
        body.setMsg(e.getReason());
        return org.springframework.http.ResponseEntity.status(e.getStatusCode()).body(body);
    }

    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public ResponseData<?> bizExceptionHandler(CustomException e) {
        ResponseData<?> responseData = new ResponseData<>();
        responseData.setCode(SysConst.STATUS_500);
        responseData.setMsg(e.getErrorMsg());
        responseData.setSuccess(false);
        return responseData;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseData<?> exceptionHandler(Exception e) {
        String message = "系统异常,请联系管理员";
        if (e instanceof org.mybatis.spring.MyBatisSystemException) {
            org.mybatis.spring.MyBatisSystemException myBatisSystemException = (org.mybatis.spring.MyBatisSystemException) e;
            message = myBatisSystemException.getCause().getMessage();
            if (message.contains("Failed to obtain JDBC")) {
                message = "数据库没有链接上,1.请检查数据库的账号和密码是否正确,2.请检查数据库是否启动,3.请检查数据库是否正常运行，4.请检查数据库配置正确";
            }
        }

        ResponseData<?> responseData = new ResponseData<>();
        responseData.setCode(SysConst.STATUS_500);
        e.printStackTrace();
        System.out.println(e.getMessage());
        responseData.setMsg(message);
        responseData.setSuccess(false);
        return responseData;
    }
}
