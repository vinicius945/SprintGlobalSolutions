package com.fiap.gestaoltakn.controller.departamento;

import com.fiap.gestaoltakn.dto.DepartamentoDTO;
import com.fiap.gestaoltakn.entity.DepartamentoEntity;
import com.fiap.gestaoltakn.mapper.DepartamentoMapper;
import com.fiap.gestaoltakn.service.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/departamentos")
public class DepartamentoWebController {

    private final DepartamentoService departamentoService;

    public DepartamentoWebController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "search", required = false) String search, Model model) {
        List<DepartamentoEntity> todos = departamentoService.listarTodos();

        List<DepartamentoDTO> resultado;
        if (search == null || search.trim().isEmpty()) {
            resultado = todos.stream().map(DepartamentoMapper::toDepartamentoDTO).collect(Collectors.toList());
        } else {
            String q = search.trim();
            String qLower = q.toLowerCase(Locale.ROOT);

            resultado = todos.stream()
                    .filter(dep -> {
                        try {
                            Long id = Long.parseLong(q);
                            if (dep.getId() != null && dep.getId().equals(id)) return true;
                        } catch (NumberFormatException ignored) {}

                        try {
                            Integer horas = Integer.valueOf(q);
                            if (dep.getNumeroHorasMaximas() != null && dep.getNumeroHorasMaximas().equals(horas)) return true;
                        } catch (NumberFormatException ignored) {}

                        if (dep.getNome() != null && dep.getNome().toLowerCase(Locale.ROOT).contains(qLower)) return true;

                        return false;
                    })
                    .map(DepartamentoMapper::toDepartamentoDTO)
                    .collect(Collectors.toList());
        }

        model.addAttribute("departamentos", resultado);
        model.addAttribute("search", search);
        return "departamentos/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("departamento", new DepartamentoDTO());
        return "departamentos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("departamento") @Valid DepartamentoDTO departamentoDto, BindingResult result) {
        if (result.hasErrors()) {
            return "departamentos/form";
        }
        DepartamentoEntity entity = DepartamentoMapper.toDepartamentoEntity(departamentoDto);
        if (departamentoDto.getId() == null) {
            departamentoService.criar(entity);
        } else {
            departamentoService.atualizar(departamentoDto.getId(), entity);
        }
        return "redirect:/departamentos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        DepartamentoEntity entity = departamentoService.buscarPorId(id);
        model.addAttribute("departamento", DepartamentoMapper.toDepartamentoDTO(entity));
        return "departamentos/form";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id) {
        departamentoService.deletar(id);
        return "redirect:/departamentos";
    }

}
