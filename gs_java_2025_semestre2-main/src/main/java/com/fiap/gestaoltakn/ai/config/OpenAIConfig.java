package com.fiap.gestaoltakn.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAIConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIConfig.class);

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.groq.com/openai/v1}")
    private String openaiApiUrl;

    @Bean
    public WebClient openaiWebClient() {
        logger.info("Configurando WebClient para Groq API - URL: {}", openaiApiUrl);
        logger.info("API Key configurada: {}", openaiApiKey != null && !openaiApiKey.isEmpty());

        return WebClient.builder()
                .baseUrl(openaiApiUrl)
                .defaultHeader("Authorization", "Bearer " + openaiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

}
