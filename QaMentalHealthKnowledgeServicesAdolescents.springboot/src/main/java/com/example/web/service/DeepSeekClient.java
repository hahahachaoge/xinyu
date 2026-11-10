package com.example.web.service;

import com.example.web.config.DeepSeekConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class DeepSeekClient {
    private final DeepSeekConfig config;
    private final ObjectMapper json;
    public record Reply(String content, Integer tokens, String requestId, String model) { }

    public Reply complete(List<Map<String,String>> messages, Consumer<String> onContent) {
        HttpURLConnection connection = null;
        try {
            if (config.getApiKey() == null || config.getApiKey().isBlank()) throw failure("DeepSeek Key 未配置");
            boolean stream = onContent != null;
            Map<String,Object> body = new LinkedHashMap<>();
            body.put("model",config.getModelName()); body.put("messages",messages);
            body.put("stream",stream); body.put("temperature",0.3); body.put("max_tokens",2000);
            if (stream) body.put("stream_options",Map.of("include_usage",true));
            connection = (HttpURLConnection)new URL(config.getCompletionsUrl()).openConnection();
            connection.setRequestMethod("POST");connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Authorization","Bearer " + config.getApiKey());
            connection.setConnectTimeout(15000);connection.setReadTimeout(120000);
            try(var out=connection.getOutputStream()) {json.writeValue(out,body);}
            int status=connection.getResponseCode();
            if(status != 200) {
                String reason = switch(status) {
                    case 401,403 -> "DeepSeek 身份验证失败，请检查服务端 Key";
                    case 402 -> "DeepSeek 余额不足";
                    case 429 -> "DeepSeek 请求过于频繁，请稍后重试";
                    default -> "DeepSeek 服务调用失败（HTTP " + status + "）";
                };
                throw failure(reason);
            }
            try(var reader=new BufferedReader(new InputStreamReader(connection.getInputStream(),StandardCharsets.UTF_8))) {
                if(!stream) {
                    JsonNode root=json.readTree(reader);
                    checkFinish(root.path("choices").path(0));
                    String content=root.path("choices").path(0).path("message").path("content").asText("");
                    if(content.isBlank() || root.has("error")) throw failure("DeepSeek 返回了空回复或无效数据");
                    return new Reply(content,tokens(root),root.path("id").asText(),root.path("model").asText());
                }
                StringBuilder text=new StringBuilder();String line,id="",model="";Integer tokens=null;boolean done=false;
                while((line=reader.readLine())!=null) {
                    if(!line.startsWith("data:")) continue;
                    String data=line.substring(5).trim();
                    if(data.equals("[DONE]")) {done=true;break;}
                    if(data.isEmpty()) continue;
                    JsonNode node=json.readTree(data);
                    if(node.has("error")) throw failure("DeepSeek 流式回复失败");
                    checkFinish(node.path("choices").path(0));
                    if(node.has("id")) id=node.path("id").asText();
                    if(node.has("model")) model=node.path("model").asText();
                    if(tokens(node)!=null) tokens=tokens(node);
                    String delta=node.path("choices").path(0).path("delta").path("content").asText("");
                    if(!delta.isEmpty()) {text.append(delta);onContent.accept(delta);}
                }
                if(!done || text.toString().isBlank()) throw failure("DeepSeek 回复中断，请重试；未保存不完整回复");
                return new Reply(text.toString(),tokens,id,model);
            }
        } catch(ResponseStatusException e) {throw e;}
        catch(Exception e) {throw failure("DeepSeek 连接失败或回复中断，请稍后重试");}
        finally {if(connection!=null) connection.disconnect();}
    }
    private Integer tokens(JsonNode node) {
        JsonNode value=node.path("usage").path("total_tokens");return value.isIntegralNumber()?value.intValue():null;
    }
    private void checkFinish(JsonNode choice) {
        String reason=choice.path("finish_reason").asText("");
        if(!reason.isEmpty() && !reason.equals("stop"))
            throw failure(reason.equals("length")?"回复达到长度限制，请缩短问题后重试":"DeepSeek 未完成回复，请稍后重试");
    }
    private ResponseStatusException failure(String reason) {return new ResponseStatusException(HttpStatus.BAD_GATEWAY,reason);}
}
