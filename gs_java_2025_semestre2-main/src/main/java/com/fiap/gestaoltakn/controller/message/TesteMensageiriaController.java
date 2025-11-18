package com.fiap.gestaoltakn.controller.message;

import com.fiap.gestaoltakn.service.message.CacheSyncProducer;
import com.fiap.gestaoltakn.service.message.RelatorioProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teste-mensageiria")
public class TesteMensageiriaController {

    private final CacheSyncProducer cacheSyncProducer;
    private final RelatorioProducer relatorioProducer;

    @Value("${spring.rabbitmq.host:localhost}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port:5672}")
    private String rabbitPort;

    @Value("${spring.rabbitmq.username:guest}")
    private String rabbitUser;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String rabbitVirtualHost;

    public TesteMensageiriaController(CacheSyncProducer cacheSyncProducer, RelatorioProducer relatorioProducer) {
        this.cacheSyncProducer = cacheSyncProducer;
        this.relatorioProducer = relatorioProducer;
    }

    @GetMapping
    public String paginaTeste(Model model) {
        model.addAttribute("rabbitHost", rabbitHost + ":" + rabbitPort);
        model.addAttribute("rabbitUser", rabbitUser);
        model.addAttribute("rabbitVirtualHost", rabbitVirtualHost);

        String webInterface = determineWebInterfaceUrl();
        model.addAttribute("rabbitWebInterface", webInterface);

        return "teste-mensageiria";
    }

    @PostMapping("/cache-sync")
    public String testarCacheSync(RedirectAttributes redirectAttributes) {
        try {
            cacheSyncProducer.enviarInvalidacaoDepartamento(1L);
            cacheSyncProducer.enviarInvalidacaoFuncionario(1L);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Mensagens de sincronização de cache enviadas com sucesso! Verifique os logs.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erro ao enviar mensagens: " + e.getMessage());
        }
        return "redirect:/teste-mensageiria";
    }

    @PostMapping("/relatorio")
    public String testarRelatorio(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String usuario = authentication != null ? authentication.getName() : "usuario-teste";
            relatorioProducer.solicitarRelatorioFuncionariosRisco(usuario);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Relatório solicitado com sucesso! Processamento assíncrono iniciado. Verifique os logs.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erro ao solicitar relatório: " + e.getMessage());
        }
        return "redirect:/teste-mensageiria";
    }

    private String determineWebInterfaceUrl() {
        if (rabbitHost.contains("cloudamqp.com")) {
            String hostWithoutPort = rabbitHost.split(":")[0];
            return "https://" + hostWithoutPort.replace("fly-", "") + ".cloudamqp.com";
        }
        return "http://localhost:15672";
    }

}
