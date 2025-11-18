package com.fiap.gestaoltakn.service.message;

import com.fiap.gestaoltakn.config.RabbitMQConfig;
import com.fiap.gestaoltakn.dto.message.RelatorioMessage;
import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import com.fiap.gestaoltakn.enums.FuncionarioStatus;
import com.fiap.gestaoltakn.repository.FuncionarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RelatorioConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioConsumer.class);

    private final FuncionarioRepository funcionarioRepository;
    private final EmailSimuladoService emailService;

    public RelatorioConsumer(FuncionarioRepository funcionarioRepository, EmailSimuladoService emailService) {
        this.funcionarioRepository = funcionarioRepository;
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.RELATORIO_QUEUE)
    public void processarRelatorio(RelatorioMessage message) {
        logger.info("Iniciando processamento de relatório: {}", message.getTipoRelatorio());

        try {
            if ("FUNCIONARIOS_EM_RISCO".equals(message.getTipoRelatorio())) {
                processarRelatorioFuncionariosRisco(message);
            }
        } catch (Exception e) {
            logger.error("Erro ao processar relatório: {}", e.getMessage(), e);
        }
    }

    private void processarRelatorioFuncionariosRisco(RelatorioMessage message) {
        List<FuncionarioEntity> funcionariosEmRisco = funcionarioRepository.findByStatus(FuncionarioStatus.EM_RISCO);

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== RELATÓRIO DE FUNCIONÁRIOS EM RISCO ===\n");
        relatorio.append("Data de geração: ").append(java.time.LocalDateTime.now()).append("\n");
        relatorio.append("Total de funcionários em risco: ").append(funcionariosEmRisco.size()).append("\n\n");

        for (FuncionarioEntity funcionario : funcionariosEmRisco) {
            relatorio.append("ID: ").append(funcionario.getId()).append("\n");
            relatorio.append("Nome: ").append(funcionario.getNome()).append("\n");
            relatorio.append("Departamento: ").append(funcionario.getDepartamento().getNome()).append("\n");
            relatorio.append("Horas trabalhadas: ").append(funcionario.getHorasTrabalhadasUltimoMes()).append("\n");
            relatorio.append("Limite do departamento: ").append(funcionario.getDepartamento().getNumeroHorasMaximas()).append("\n");
            relatorio.append("----------------------------------------\n");
        }

        logger.info("Relatório gerado com sucesso: {} funcionários em risco", funcionariosEmRisco.size());

        emailService.enviarRelatorioPorEmail(message.getUsuarioSolicitante(), relatorio.toString());
    }

}
