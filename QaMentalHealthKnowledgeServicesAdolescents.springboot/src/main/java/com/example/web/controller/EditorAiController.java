package com.example.web.controller;

import com.example.web.common.CurrentUserDto;
import com.example.web.config.DeepSeekConfig;
import com.example.web.enums.RoleTypeEnum;
import com.example.web.exception.CustomException;
import com.example.web.utils.BaseContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/** 富文本编辑器通过本项目后端调用模型，客户端不持有供应商密钥。 */
@RestController
@RequestMapping("/editorAi")
public class EditorAiController {
    @Autowired
    private DeepSeekConfig config;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/complete")
    public Map<String, String> complete(@RequestBody EditorInput input) {
        CurrentUserDto user = BaseContext.getCurrentUserDto();
        if (user == null || user.getUserId() == null || user.getRoleType() != RoleTypeEnum.管理员) {
            throw new CustomException("请使用管理员账号登录后操作");
        }
        if (input.getPrompt() == null || input.getPrompt().isBlank() || input.getPrompt().length() > 20000) {
            throw new CustomException("请输入有效内容，长度不能超过20000字");
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(config.getCompletionsUrl()).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(120000);
            connection.setDoOutput(true);
            byte[] body = objectMapper.writeValueAsBytes(Map.of(
                    "model", config.getModelName(), "stream", false, "max_tokens", 2000,
                    "messages", List.of(Map.of("role", "user", "content", input.getPrompt()))));
            try (var output = connection.getOutputStream()) {
                output.write(body);
            }
            if (connection.getResponseCode() != 200) {
                throw new CustomException("AI服务暂时不可用，请稍后重试");
            }
            JsonNode response;
            try (var stream = connection.getInputStream()) {
                response = objectMapper.readTree(stream);
            }
            String content = response.path("choices").path(0).path("message").path("content").asText();
            if (content.isBlank()) throw new CustomException("AI未返回有效内容，请重试");
            return Map.of("content", content);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("AI请求失败，请稍后重试");
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    @Data
    public static class EditorInput {
        private String prompt;
    }
}
