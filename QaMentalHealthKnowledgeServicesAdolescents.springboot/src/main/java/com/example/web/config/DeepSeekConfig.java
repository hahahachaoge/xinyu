package com.example.web.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {
    private String apiKey;
    private String baseUrl;
    private String modelName;

    public String getCompletionsUrl() {
        return baseUrl.replaceAll("/+$", "") + "/chat/completions";
    }
}
