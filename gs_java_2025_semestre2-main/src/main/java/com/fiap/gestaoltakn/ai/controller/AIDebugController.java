package com.fiap.gestaoltakn.ai.controller;

import com.fiap.gestaoltakn.ai.dto.OpenAIRequest;
import com.fiap.gestaoltakn.ai.dto.OpenAIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/debug")
public class AIDebugController {

    private static final Logger logger = LoggerFactory.getLogger(AIDebugController.class);

    private final WebClient openaiWebClient;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.api.url:}")
    private String apiUrl;

    public AIDebugController(WebClient openaiWebClient) {
        this.openaiWebClient = openaiWebClient;
    }

    @GetMapping("/available-models")
    public ResponseEntity<Map<String, Object>> getAvailableModels() {
        return ResponseEntity.ok(Map.of(
                "available_models", List.of(
                        "llama-3.1-8b-instant",
                        "llama-3.2-1b-preview",
                        "llama-3.2-3b-preview",
                        "llama-3.2-90b-vision-preview",
                        "llama-3.2-11b-vision-preview",
                        "gemma2-9b-it"
                ),
                "recommended", "llama-3.1-8b-instant",
                "api_url", apiUrl,
                "api_key_configured", openaiApiKey != null && !openaiApiKey.isEmpty(),
                "note", "O modelo llama3-8b-8192 foi descontinuado. Use os modelos acima."
        ));
    }

    @GetMapping("/test-simple")
    public ResponseEntity<Map<String, Object>> testSimple() {
        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "API Key não configurada",
                    "apiKeyConfigured", false
            ));
        }

        try {
            OpenAIRequest request = new OpenAIRequest(
                    "llama-3.1-8b-instant",
                    List.of(new OpenAIRequest.Message("user", "Responda apenas com 'OK' se estiver funcionando.")),
                    0.7,
                    null
            );

            logger.info("Testando API Groq com modelo: {}", request.getModel());

            OpenAIResponse response = openaiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .block();

            if (response != null &&
                    response.getChoices() != null &&
                    !response.getChoices().isEmpty()) {

                String content = response.getChoices().get(0).getMessage().getContent();
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "API Groq funcionando!",
                        "response", content,
                        "model", request.getModel(),
                        "apiKeyConfigured", true
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "error",
                        "message", "Resposta vazia da API",
                        "apiKeyConfigured", true
                ));
            }

        } catch (WebClientResponseException e) {
            logger.error("Erro HTTP {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Erro HTTP: " + e.getStatusCode(),
                    "errorDetails", e.getResponseBodyAsString(),
                    "apiKeyConfigured", true,
                    "apiUrl", apiUrl
            ));
        } catch (Exception e) {
            logger.error("Erro inesperado: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Erro: " + e.getMessage(),
                    "apiKeyConfigured", true
            ));
        }
    }

    @GetMapping("/test-models")
    public ResponseEntity<Map<String, Object>> testDifferentModels() {
        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            return ResponseEntity.ok(Map.of("status", "error", "message", "API Key não configurada"));
        }

        List<String> modelsToTest = List.of(
                "llama-3.1-8b-instant",
                "llama-3.2-1b-preview",
                "llama-3.2-3b-preview",
                "gemma2-9b-it"
        );

        Map<String, Object> results = new HashMap<>();

        for (String model : modelsToTest) {
            try {
                OpenAIRequest request = new OpenAIRequest(
                        model,
                        List.of(new OpenAIRequest.Message("user", "Responda com 'OK'")),
                        0.7,
                        null
                );

                OpenAIResponse response = openaiWebClient.post()
                        .uri("/chat/completions")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(OpenAIResponse.class)
                        .block();

                if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                    results.put(model, Map.of(
                            "status", "success",
                            "response", response.getChoices().get(0).getMessage().getContent()
                    ));
                } else {
                    results.put(model, Map.of("status", "empty_response"));
                }
            } catch (Exception e) {
                results.put(model, Map.of(
                        "status", "error",
                        "error", e.getMessage()
                ));
            }
        }

        return ResponseEntity.ok(Map.of(
                "model_tests", results,
                "note", "Testando diferentes modelos disponíveis no Groq"
        ));
    }

    @GetMapping("/test-minimal")
    public ResponseEntity<Map<String, Object>> testMinimal() {
        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            return ResponseEntity.ok(Map.of("status", "error", "message", "API Key não configurada"));
        }

        try {
            Map<String, Object> request = Map.of(
                    "model", "llama-3.1-8b-instant",
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", "Diga apenas OK"
                    )),
                    "temperature", 0.1,
                    "max_tokens", 10
            );

            logger.info("Testando com request mínimo...");

            Map response = openaiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Request mínimo funcionou!",
                    "response", response
            ));

        } catch (Exception e) {
            logger.error("Erro no teste mínimo: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Erro: " + e.getMessage()
            ));
        }
    }

}
