package com.example.web.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SelectResult {

    private String name;

    private String label;

    private String code;

    private Object prop;
}
