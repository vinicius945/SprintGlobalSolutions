package com.fiap.gestaoltakn.service.message;

import com.fiap.gestaoltakn.config.RabbitMQConfig;
import com.fiap.gestaoltakn.dto.message.RelatorioMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class RelatorioProducer {

    private final RabbitTemplate rabbitTemplate;

    public RelatorioProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void solicitarRelatorioFuncionariosRisco(String usuarioSolicitante) {
        RelatorioMessage message = new RelatorioMessage(
                "FUNCIONARIOS_EM_RISCO",
                LocalDateTime.now(),
                usuarioSolicitante,
                "{\"status\": \"EM_RISCO\"}"
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                "relatorio.funcionarios.risco",
                message
        );
    }

}
