package com.fiap.gestaoltakn.service.message;

import com.fiap.gestaoltakn.config.RabbitMQConfig;
import com.fiap.gestaoltakn.dto.message.CacheSyncMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheSyncProducer {

    private final RabbitTemplate rabbitTemplate;

    public CacheSyncProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarMensagemCacheSync(CacheSyncMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                "cache.sync.departamento",
                message
        );
    }

    public void enviarInvalidacaoDepartamento(Long departamentoId) {
        CacheSyncMessage message = new CacheSyncMessage(
                "departamentos",
                "all",
                "EVICT",
                departamentoId,
                "DEPARTAMENTO"
        );
        enviarMensagemCacheSync(message);
    }

    public void enviarInvalidacaoFuncionario(Long funcionarioId) {
        CacheSyncMessage message = new CacheSyncMessage(
                "funcionarios",
                "page:*",
                "EVICT",
                funcionarioId,
                "FUNCIONARIO"
        );
        enviarMensagemCacheSync(message);
    }

}
