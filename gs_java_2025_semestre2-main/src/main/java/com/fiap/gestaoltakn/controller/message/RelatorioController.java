package com.fiap.gestaoltakn.controller.message;

import com.fiap.gestaoltakn.service.message.RelatorioProducer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioProducer relatorioProducer;

    public RelatorioController(RelatorioProducer relatorioProducer) {
        this.relatorioProducer = relatorioProducer;
    }

    @PostMapping("/funcionarios-risco")
    public String solicitarRelatorioFuncionariosRisco(Authentication authentication, RedirectAttributes redirectAttributes) {
        String usuario = authentication.getName();

        relatorioProducer.solicitarRelatorioFuncionariosRisco(usuario);

        redirectAttributes.addFlashAttribute("successMessage",
                "Relatório de funcionários em risco solicitado com sucesso! Você receberá por email em instantes.");

        return "redirect:/funcionarios";
    }

}
