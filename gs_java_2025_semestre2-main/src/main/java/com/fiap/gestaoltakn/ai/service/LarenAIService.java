package com.fiap.gestaoltakn.ai.service;

import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class LarenAIService {

    private static final Logger logger = LoggerFactory.getLogger(LarenAIService.class);
    private final WebClient webClient;

    @Value("${ai.python.service.url}")
    private String pythonApiUrl;

    public LarenAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String analisarFuncionario(FuncionarioEntity funcionario) {
        logger.info("🚀 Laren AI analisando: {}", funcionario.getNome());

        try {
            String prompt = String.format(
                    "Analise o funcionário %s. Depto: %s. Horas: %d (Max: %d). Status: %s. Dê recomendações curtas.",
                    funcionario.getNome(),
                    funcionario.getDepartamento().getNome(),
                    funcionario.getHorasTrabalhadasUltimoMes(),
                    funcionario.getDepartamento().getNumeroHorasMaximas(),
                    funcionario.getStatus()
            );

            Map<String, String> body = new HashMap<>();
            body.put("mensagem", prompt);

            Map response = webClient.post()
                    .uri(pythonApiUrl)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("resposta")) {
                return response.get("resposta").toString();
            }
            return "Sem resposta da Laren.";

        } catch (Exception e) {
            logger.error("Erro na conexão com Python: {}", e.getMessage());
            return "Erro: O Chatbot Laren não está respondendo. Verifique o terminal Python.";
        }
    }
}
