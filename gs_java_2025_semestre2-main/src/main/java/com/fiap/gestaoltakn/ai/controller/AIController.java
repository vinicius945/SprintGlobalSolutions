package com.fiap.gestaoltakn.ai.controller;

import com.fiap.gestaoltakn.ai.dto.AnaliseBemEstarDTO;
import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import com.fiap.gestaoltakn.service.FuncionarioService;
import com.fiap.gestaoltakn.ai.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;
    private final FuncionarioService funcionarioService;

    public AIController(AIService aiService, FuncionarioService funcionarioService) {
        this.aiService = aiService;
        this.funcionarioService = funcionarioService;
    }

    @GetMapping("/funcionarios/{id}/analise-bem-estar")
    public ResponseEntity<AnaliseBemEstarDTO> analisarBemEstarFuncionario(@PathVariable Long id) {
        try {
            FuncionarioEntity funcionario = funcionarioService.buscarPorId(id);
            String analise = aiService.analisarBemEstarFuncionario(funcionario);

            AnaliseBemEstarDTO response = new AnaliseBemEstarDTO(
                    funcionario.getId(),
                    funcionario.getNome(),
                    analise,
                    "Recomendações incluídas na análise"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/resumo-equipe")
    public ResponseEntity<Map<String, Object>> gerarResumoEquipe() {
        try {
            var todosFuncionarios = funcionarioService.listarTodos();
            long totalFuncionarios = todosFuncionarios.size();
            long emRisco = todosFuncionarios.stream()
                    .filter(f -> f.getStatus() != null && f.getStatus().name().equals("EM_RISCO"))
                    .count();
            long saudaveis = totalFuncionarios - emRisco;

            String resumo = aiService.gerarResumoEquipe(totalFuncionarios, emRisco, saudaveis);

            Map<String, Object> response = new HashMap<>();
            response.put("resumo", resumo);
            response.put("totalFuncionarios", totalFuncionarios);
            response.put("emRisco", emRisco);
            response.put("saudaveis", saudaveis);
            response.put("status", "success");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("resumo", "Sistema de IA temporariamente indisponível. Entre em contato com o suporte.");
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/departamentos/{departamentoId}/recomendacoes")
    public ResponseEntity<String> gerarRecomendacoesDepartamento(@PathVariable Long departamentoId) {
        try {
            String recomendacoes = aiService.gerarRecomendacoesDepartamento(
                    "Tecnologia",
                    160,
                    15L,
                    3L
            );

            return ResponseEntity.ok(recomendacoes);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao gerar recomendações");
        }
    }

}
