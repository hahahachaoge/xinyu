package com.example.web.service;

import com.example.web.config.DeepSeekConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class DeepSeekClientTest {
    HttpServer server;DeepSeekClient client;String body;int status=200;String received;
    @BeforeEach void setup() throws Exception {
        server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/chat/completions",exchange->{
            received=new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);
            byte[] data=body.getBytes(StandardCharsets.UTF_8);exchange.sendResponseHeaders(status,data.length);
            // Deliberately split UTF-8 and JSON across network writes.
            for(byte b:data)exchange.getResponseBody().write(b);
            exchange.close();
        });server.start();
        var config=new DeepSeekConfig();config.setBaseUrl("http://127.0.0.1:"+server.getAddress().getPort());
        config.setApiKey("unit-test-only-key");config.setModelName("test-model");client=new DeepSeekClient(config,new ObjectMapper());
    }
    @AfterEach void stop(){server.stop(0);}
    List<Map<String,String>> messages(){return List.of(Map.of("role","user","content","睡眠\\n\"问题\"\t测试"));}
    @Test void preservesReplyAndUsageAndBuildsValidJson() throws Exception {
        body="{\"id\":\"request-1\",\"model\":\"test-model\",\"choices\":[{\"message\":{\"content\":\"真实回复\"}}],\"usage\":{\"total_tokens\":12}}";
        var reply=client.complete(messages(),null);assertEquals("真实回复",reply.content());assertEquals(12,reply.tokens());
        assertEquals(messages().get(0).get("content"),new ObjectMapper().readTree(received).path("messages").path(0).path("content").asText());
    }
    @Test void streamsContentAndUsageIncludingEmptyChoicesChunk() {
        body="data: {\"id\":\"r\",\"choices\":[{\"delta\":{\"content\":\"睡眠\"}}]}\n\n"
            +"data: {\"choices\":[],\"usage\":{\"total_tokens\":20}}\n\ndata: [DONE]\n\n";
        var parts=new ArrayList<String>();var reply=client.complete(messages(),parts::add);
        assertEquals(List.of("睡眠"),parts);assertEquals(20,reply.tokens());
    }
    @Test void rejectsTruncatedStream() {
        body="data: {\"choices\":[{\"delta\":{\"content\":\"partial\"}}]}\n\n";
        assertThrows(ResponseStatusException.class,()->client.complete(messages(),s->{}));
    }
    @Test void neverTurnsProviderErrorIntoAnAnswerOrLeaksResponseBody() {
        status=402;body="private upstream details unit-test-only-key";
        var error=assertThrows(ResponseStatusException.class,()->client.complete(messages(),null));
        assertEquals(502,error.getStatusCode().value());assertTrue(error.getReason().contains("余额不足"));
        assertFalse(error.getReason().contains("unit-test-only-key"));
    }
    @Test void rejectsEmptyModelReply() {
        body="{\"choices\":[{\"message\":{\"content\":\"\"}}]}";
        assertThrows(ResponseStatusException.class,()->client.complete(messages(),null));
    }
    @Test void rejectsProviderAbortEvenWhenDoneMarkerArrives() {
        body="data: {\"choices\":[{\"delta\":{\"content\":\"partial\"},\"finish_reason\":\"aborted\"}]}\n\ndata: [DONE]\n\n";
        assertThrows(ResponseStatusException.class,()->client.complete(messages(),s->{}));
    }
}
