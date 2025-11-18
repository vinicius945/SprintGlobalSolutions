package com.fiap.gestaoltakn.service.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailSimuladoService {

    private static final Logger logger = LoggerFactory.getLogger(EmailSimuladoService.class);

    public void enviarRelatorioPorEmail(String destinatario, String relatorio) {
        logger.info("=== EMAIL SIMULADO ENVIADO ===");
        logger.info("Para: {}", destinatario);
        logger.info("Assunto: Relatório de Funcionários em Risco");
        logger.info("Conteúdo:\n{}", relatorio);
        logger.info("=== FIM DO EMAIL ===");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
