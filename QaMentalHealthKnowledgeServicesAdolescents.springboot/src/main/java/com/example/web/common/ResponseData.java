package com.example.web.common;

import com.example.web.SysConst;

import lombok.Data;

@Data
public class ResponseData<T> {
    public String msg;

    public T data;

    public String code;

    public Boolean success;

    public static <T> ResponseData<T> getResponseDataInstance(T data, String msg, Boolean success) {
        ResponseData<T> responseData = new ResponseData<>();
        responseData.setSuccess(success);
        responseData.setData(data);
        responseData.setMsg(msg);
        responseData.setCode(SysConst.STATUS_200);
        return responseData;
    }

    public static ResponseData<Void> ofSuccess() {
        ResponseData<Void> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMsg("成功");
        responseData.setCode(SysConst.STATUS_200);
        return responseData;
    }
}
