package com.fiap.gestaoltakn.ai.service;

import com.fiap.gestaoltakn.ai.dto.OpenAIRequest;
import com.fiap.gestaoltakn.ai.dto.OpenAIResponse;
import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final WebClient openaiWebClient;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.api.url:}")
    private String openaiApiUrl;

    public AIService(WebClient openaiWebClient) {
        this.openaiWebClient = openaiWebClient;
    }

    @PostConstruct
    public void init() {
        logger.info("=== CONFIGURAÇÃO GROQ AI ===");
        logger.info("API URL: {}", openaiApiUrl);
        logger.info("API Key configurada: {}", openaiApiKey != null && !openaiApiKey.isEmpty());
        logger.info("============================");
    }

    public String analisarBemEstarFuncionario(FuncionarioEntity funcionario) {
        logger.info("Iniciando análise de bem-estar para funcionário: {}", funcionario.getNome());

        boolean apiKeyConfigurada = openaiApiKey != null && !openaiApiKey.isEmpty();
        logger.debug("API Key configurada: {}", apiKeyConfigurada);
        logger.debug("API URL: {}", openaiApiUrl);

        if (!apiKeyConfigurada) {
            logger.warn("API Key não configurada, usando análise mock");
            return gerarAnaliseMock(funcionario);
        }

        try {
            String prompt = String.format("""
                Analise o bem-estar do funcionário com base nos dados abaixo e forneça recomendações concisas:
                
                Nome: %s
                Departamento: %s
                Horas trabalhadas no último mês: %d
                Limite máximo de horas do departamento: %d
                Status atual: %s
                
                Forneça:
                1. Uma breve análise do nível de risco
                2. 2-3 recomendações específicas para melhorar o bem-estar
                3. Sugestões para o gestor do departamento
                
                Seja objetivo e use no máximo 300 palavras. Responda em português brasileiro.
                """,
                    funcionario.getNome(),
                    funcionario.getDepartamento().getNome(),
                    funcionario.getHorasTrabalhadasUltimoMes(),
                    funcionario.getDepartamento().getNumeroHorasMaximas(),
                    funcionario.getStatus().name()
            );

            logger.info("Enviando requisição para Groq API...");

            OpenAIRequest request = new OpenAIRequest(
                    "llama-3.1-8b-instant",
                    List.of(new OpenAIRequest.Message("user", prompt)),
                    0.7,
                    500
            );

            OpenAIResponse response = openaiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .block();

            if (response != null &&
                    response.getChoices() != null &&
                    !response.getChoices().isEmpty() &&
                    response.getChoices().get(0).getMessage() != null &&
                    response.getChoices().get(0).getMessage().getContent() != null) {

                String resultado = response.getChoices().get(0).getMessage().getContent();
                logger.info("Resposta da IA recebida com sucesso! Tamanho: {} caracteres", resultado.length());
                return resultado;
            } else {
                logger.warn("Resposta vazia ou inválida da API Groq, usando análise mock");
                return gerarAnaliseMock(funcionario);
            }

        } catch (WebClientResponseException e) {
            logger.error("Erro HTTP na chamada da API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return gerarAnaliseMock(funcionario);
        } catch (Exception e) {
            logger.error("Erro inesperado ao analisar bem-estar do funcionário via IA: {}", e.getMessage());
            return gerarAnaliseMock(funcionario);
        }
    }

    public String gerarRecomendacoesDepartamento(String nomeDepartamento, Integer horasMaximas,
                                                 Long totalFuncionarios, Long funcionariosEmRisco) {
        logger.info("Gerando recomendações para departamento: {}", nomeDepartamento);

        boolean apiKeyConfigurada = openaiApiKey != null && !openaiApiKey.isEmpty();
        if (!apiKeyConfigurada) {
            logger.warn("API Key não configurada, usando recomendações mock");
            return gerarRecomendacoesMock(nomeDepartamento, horasMaximas, totalFuncionarios, funcionariosEmRisco);
        }

        try {
            String prompt = String.format("""
                Com base nos dados do departamento abaixo, forneça recomendações para melhorar a gestão de carga de trabalho:
                
                Departamento: %s
                Limite máximo de horas: %d
                Total de funcionários: %d
                Funcionários em situação de risco: %d
                
                Forneça:
                1. Análise da situação atual
                2. Recomendações para redistribuição de carga
                3. Sugestões de políticas preventivas
                4. Indicações de quando considerar contratações
                
                Seja prático e objetivo, com foco em ações implementáveis.
                Use no máximo 400 palavras. Responda em português brasileiro.
                """,
                    nomeDepartamento,
                    horasMaximas,
                    totalFuncionarios,
                    funcionariosEmRisco
            );

            OpenAIRequest request = new OpenAIRequest(
                    "llama-3.1-8b-instant",
                    List.of(new OpenAIRequest.Message("user", prompt)),
                    0.7,
                    500
            );

            OpenAIResponse response = openaiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .block();

            if (response != null &&
                    response.getChoices() != null &&
                    !response.getChoices().isEmpty() &&
                    response.getChoices().get(0).getMessage() != null &&
                    response.getChoices().get(0).getMessage().getContent() != null) {

                String resultado = response.getChoices().get(0).getMessage().getContent();
                return resultado;
            } else {
                return gerarRecomendacoesMock(nomeDepartamento, horasMaximas, totalFuncionarios, funcionariosEmRisco);
            }

        } catch (Exception e) {
            logger.error("Erro inesperado ao gerar recomendações para departamento via IA: {}", e.getMessage());
            return gerarRecomendacoesMock(nomeDepartamento, horasMaximas, totalFuncionarios, funcionariosEmRisco);
        }
    }

    public String gerarResumoEquipe(long totalFuncionarios, long emRisco, long saudaveis) {
        logger.info("Gerando resumo da equipe: total={}, emRisco={}, saudaveis={}",
                totalFuncionarios, emRisco, saudaveis);

        boolean apiKeyConfigurada = openaiApiKey != null && !openaiApiKey.isEmpty();
        if (!apiKeyConfigurada) {
            logger.warn("API Key não configurada, usando resumo mock");
            return gerarResumoEquipeMock(totalFuncionarios, emRisco, saudaveis);
        }

        try {
            String prompt = String.format("""
                Com base nos dados da equipe abaixo, forneça um resumo conciso e recomendações práticas:
                
                Estatísticas da Equipe:
                - Total de funcionários: %d
                - Funcionários saudáveis: %d
                - Funcionários em situação de risco: %d
                - Percentual em risco: %.1f%%
                
                Forneça:
                1. Uma breve análise da situação geral da equipe
                2. 3-4 recomendações prioritárias para gestão de bem-estar
                3. Sugestões de ações preventivas
                
                Seja prático, objetivo e focado em ações implementáveis.
                Use no máximo 250 palavras. Responda em português brasileiro.
                """,
                    totalFuncionarios,
                    saudaveis,
                    emRisco,
                    totalFuncionarios > 0 ? (emRisco * 100.0 / totalFuncionarios) : 0
            );

            OpenAIRequest request = new OpenAIRequest(
                    "llama-3.1-8b-instant",
                    List.of(new OpenAIRequest.Message("user", prompt)),
                    0.7,
                    500
            );

            OpenAIResponse response = openaiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .block();

            if (response != null &&
                    response.getChoices() != null &&
                    !response.getChoices().isEmpty() &&
                    response.getChoices().get(0).getMessage() != null &&
                    response.getChoices().get(0).getMessage().getContent() != null) {

                String resultado = response.getChoices().get(0).getMessage().getContent();
                logger.info("Resumo da equipe recebido com sucesso! Tamanho: {} caracteres", resultado.length());
                return resultado;
            } else {
                return gerarResumoEquipeMock(totalFuncionarios, emRisco, saudaveis);
            }

        } catch (Exception e) {
            logger.error("Erro inesperado ao gerar resumo da equipe via IA: {}", e.getMessage());
            return gerarResumoEquipeMock(totalFuncionarios, emRisco, saudaveis);
        }
    }

    private String gerarAnaliseMock(FuncionarioEntity funcionario) {
        return String.format("""
            **Análise de Bem-Estar - %s** [MOCK]
            
            **Situação Atual:** 
            Funcionário trabalhou %d horas no último mês, com limite departamental de %d horas.
            Status: %s
            
            **Recomendações:**
            1. Monitorar carga de trabalho regularmente
            2. Considerar redistribuição de tarefas se necessário
            3. Promover pausas regulares durante a jornada
            """,
                funcionario.getNome(),
                funcionario.getHorasTrabalhadasUltimoMes(),
                funcionario.getDepartamento().getNumeroHorasMaximas(),
                funcionario.getStatus().name()
        );
    }

    private String gerarRecomendacoesMock(String nomeDepartamento, Integer horasMaximas,
                                          Long totalFuncionarios, Long funcionariosEmRisco) {
        return String.format("""
            **Recomendações para %s** [MOCK]
            
            **Análise:** 
            Departamento com %d funcionários, sendo %d em situação de risco.
            Limite de horas: %d
            
            **Ações Recomendadas:**
            1. Revisar distribuição de tarefas entre a equipe
            2. Implementar sistema de rodízio para cargas pesadas
            3. Estabelecer políticas de descanso obrigatório
            """,
                nomeDepartamento,
                totalFuncionarios,
                funcionariosEmRisco,
                horasMaximas
        );
    }

    private String gerarResumoEquipeMock(long totalFuncionarios, long emRisco, long saudaveis) {
        return String.format("""
            **Resumo da Equipe - Análise de Bem-Estar** [MOCK]
            
            **Situação Atual:**
            - Total de funcionários: %d
            - Em situação saudável: %d
            - Em situação de risco: %d
            - Taxa de risco: %.1f%%
            
            **Recomendações Prioritárias:**
            1. Monitorar continuamente a carga de trabalho dos %d funcionários em risco
            2. Implementar políticas de descanso obrigatório
            3. Realizar check-ins regulares sobre bem-estar
            4. Oferecer suporte psicológico preventivo
            """,
                totalFuncionarios,
                saudaveis,
                emRisco,
                totalFuncionarios > 0 ? (emRisco * 100.0 / totalFuncionarios) : 0,
                emRisco
        );
    }

}
