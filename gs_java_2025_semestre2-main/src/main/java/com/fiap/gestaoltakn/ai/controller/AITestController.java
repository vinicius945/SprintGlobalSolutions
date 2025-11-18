package com.fiap.gestaoltakn.ai.controller;

import com.fiap.gestaoltakn.ai.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/test")
public class AITestController {

    private final AIService aiService;

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.url:}")
    private String apiUrl;

    public AITestController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("apiUrl", apiUrl);
        config.put("apiKeyConfigured", apiKey != null && !apiKey.isEmpty() ? "SIM" : "NÃO (usando mock)");
        config.put("status", "IA Configurada com Groq");
        config.put("model", "llama-3.1-8b-instant");

        return ResponseEntity.ok(config);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try {
            String testResult = aiService.gerarRecomendacoesDepartamento("Teste", 160, 10L, 2L);
            boolean isUsingRealAPI = !testResult.contains("**Recomendações para Teste**");

            return ResponseEntity.ok("✅ Serviço de IA funcionando! Modo: " +
                    (isUsingRealAPI ? "Groq API Real" : "Mock (fallback)"));
        } catch (Exception e) {
            return ResponseEntity.ok("⚠️ Serviço de IA com problemas, usando mock: " + e.getMessage());
        }
    }

}
