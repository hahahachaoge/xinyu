package com.example.web.common;

import lombok.Data;

@Data
public class SortItemDto {

    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = SortFieldDeserializer.class)
    private String fieldName;

    private Boolean isAsc = false;
}
