package com.example.web.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class SortFieldDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String field = parser.getValueAsString();
        if (field == null || !field.matches("[A-Za-z_][A-Za-z0-9_]{0,63}"))
            throw context.weirdStringException(field, String.class, "排序字段格式无效");
        return field;
    }
}
