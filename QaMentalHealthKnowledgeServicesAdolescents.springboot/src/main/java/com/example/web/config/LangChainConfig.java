package com.example.web.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Langchain4j 配置类
 * 配置 Embedding 模型和向量存储
 */
@Configuration
@EnableAsync
public class LangChainConfig {

    @Value("${langchain4j.openai.embedding-api-key}")
    private String embeddingApiKey;

    @Value("${langchain4j.openai.embedding-base-url}")
    private String embeddingBaseUrl;

    @Value("${langchain4j.openai.embedding-model}")
    private String embeddingModelName;

    @Value("${langchain4j.vector-store.type:inmemory}")
    private String vectorStoreType;

    @Value("${langchain4j.pgvector.host:localhost}")
    private String pgHost;

    @Value("${langchain4j.pgvector.port:5432}")
    private int pgPort;

    @Value("${langchain4j.pgvector.database:}")
    private String pgDatabase;

    @Value("${langchain4j.pgvector.user:postgres}")
    private String pgUser;

    @Value("${langchain4j.pgvector.password:}")
    private String pgPassword;

    @Value("${langchain4j.pgvector.table:}")
    private String pgTable;

    @Value("${langchain4j.pgvector.dimension:1024}")
    private int pgDimension;

    /**
     * Embedding 模型（使用硅基流动 API）
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(embeddingApiKey)
                .baseUrl(embeddingBaseUrl)
                .modelName(embeddingModelName)
                .build();
    }

    /**
     * 向量存储
     */
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        if ("pgvector".equalsIgnoreCase(vectorStoreType)) {
            return PgVectorEmbeddingStore.builder()
                    .host(pgHost)
                    .port(pgPort)
                    .database(pgDatabase)
                    .user(pgUser)
                    .password(pgPassword)
                    .table(pgTable)
                    .dimension(pgDimension)
                    .createTable(true)
                    .build();
        }
        // 默认使用内存存储
        return new InMemoryEmbeddingStore<>();
    }
}
