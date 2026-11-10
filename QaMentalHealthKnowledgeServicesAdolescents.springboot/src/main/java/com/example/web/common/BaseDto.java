package com.example.web.common;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;

/**
 * 前端和后端进行交互的公共字段 基类
 */
@Data
public class BaseDto {

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime creationTime;

    private String behavior;

    public Boolean isEdit() {
        return id != null && id != 0 && id > 0;
    }

    public Boolean isAdd() {
        return id == null || id == 0;
    }
}
