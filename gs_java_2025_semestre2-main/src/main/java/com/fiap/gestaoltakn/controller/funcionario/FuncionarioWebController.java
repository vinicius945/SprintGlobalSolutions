package com.fiap.gestaoltakn.controller.funcionario;

import com.fiap.gestaoltakn.dto.DepartamentoDTO;
import com.fiap.gestaoltakn.dto.FuncionarioDTO;
import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import com.fiap.gestaoltakn.enums.FuncionarioStatus;
import com.fiap.gestaoltakn.mapper.DepartamentoMapper;
import com.fiap.gestaoltakn.mapper.FuncionarioMapper;
import com.fiap.gestaoltakn.service.DepartamentoService;
import com.fiap.gestaoltakn.service.FuncionarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioWebController {

    private final FuncionarioService funcionarioService;
    private final DepartamentoService departamentoService;

    public FuncionarioWebController(FuncionarioService funcionarioService, DepartamentoService departamentoService) {
        this.funcionarioService = funcionarioService;
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "search", required = false) String search,
                         @RequestParam(name = "page", defaultValue = "0") int page,
                         @RequestParam(name = "size", defaultValue = "10") int size,
                         Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FuncionarioEntity> pagina;

        if (search == null || search.trim().isEmpty()) {
            pagina = funcionarioService.listar(pageable);
        } else {
            List<FuncionarioEntity> todos = funcionarioService.listarTodos();

            String q = search.trim();
            String qLower = q.toLowerCase(Locale.ROOT);

            List<FuncionarioEntity> filtrados = todos.stream()
                    .filter(func -> filtrarFuncionario(func, q, qLower))
                    .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), filtrados.size());
            List<FuncionarioEntity> sublist = start < filtrados.size() ?
                    filtrados.subList(start, end) : List.of();

            pagina = new org.springframework.data.domain.PageImpl<>(
                    sublist, pageable, filtrados.size()
            );
        }

        List<FuncionarioDTO> resultado = pagina.getContent().stream()
                .map(FuncionarioMapper::toFuncionarioDTO)
                .collect(Collectors.toList());

        model.addAttribute("funcionarios", resultado);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", pagina.getNumber());
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("totalItems", pagina.getTotalElements());
        model.addAttribute("pageSize", size);

        return "funcionarios/list";
    }

    private boolean filtrarFuncionario(FuncionarioEntity func, String q, String qLower) {
        try {
            Long id = Long.parseLong(q);
            if (func.getId() != null && func.getId().equals(id)) return true;
        } catch (NumberFormatException ignored) {}

        try {
            Integer horas = Integer.valueOf(q);
            if (func.getHorasTrabalhadasUltimoMes() != null && func.getHorasTrabalhadasUltimoMes().equals(horas)) return true;
        } catch (NumberFormatException ignored) {}

        if (func.getStatus() != null) {
            String normalized = qLower.replaceAll("[\\s\\-]", "_").toUpperCase(Locale.ROOT);
            try {
                FuncionarioStatus s = FuncionarioStatus.valueOf(normalized);
                if (func.getStatus() == s) return true;
            } catch (IllegalArgumentException ignored) {}

            String statusLower = func.getStatus().name().toLowerCase(Locale.ROOT);
            if (statusLower.contains(qLower.replaceAll("[_\\-]", ""))) return true;

            if (qLower.equals("saudavel") && func.getStatus() == FuncionarioStatus.SAUDAVEL) return true;
            if (qLower.equals("em risco") && func.getStatus() == FuncionarioStatus.EM_RISCO) return true;
        }

        if (func.getNome() != null && func.getNome().toLowerCase(Locale.ROOT).contains(qLower)) return true;

        if (func.getDepartamento() != null && func.getDepartamento().getNome() != null
                && func.getDepartamento().getNome().toLowerCase(Locale.ROOT).contains(qLower)) return true;

        return false;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("funcionario", new FuncionarioDTO());
        List<DepartamentoDTO> deps = departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).collect(Collectors.toList());
        model.addAttribute("departamentos", deps);
        return "funcionarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("funcionario") @Valid FuncionarioDTO funcionarioDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("departamentos", departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).collect(Collectors.toList()));
            return "funcionarios/form";
        }

        try {
            var departamento = departamentoService.buscarPorId(funcionarioDto.getDepartamentoId());
            FuncionarioEntity entity = FuncionarioMapper.toFuncionarioEntity(funcionarioDto, departamento);
            if (funcionarioDto.getId() == null) {
                funcionarioService.criar(entity);
            } else {
                funcionarioService.atualizar(funcionarioDto.getId(), entity);
            }
            return "redirect:/funcionarios";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "Departamento não encontrado");
            model.addAttribute("departamentos", departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).collect(Collectors.toList()));
            return "funcionarios/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        try {
            FuncionarioEntity entity = funcionarioService.buscarPorId(id);
            FuncionarioDTO dto = FuncionarioMapper.toFuncionarioDTO(entity);
            model.addAttribute("funcionario", dto);
            model.addAttribute("departamentos", departamentoService.listarTodos().stream().map(DepartamentoMapper::toDepartamentoDTO).collect(Collectors.toList()));
            return "funcionarios/form";
        } catch (EntityNotFoundException e) {
            return "redirect:/funcionarios?error=Funcionário não encontrado";
        }
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id) {
        try {
            funcionarioService.deletar(id);
            return "redirect:/funcionarios";
        } catch (EntityNotFoundException e) {
            return "redirect:/funcionarios?error=Funcionário não encontrado";
        }
    }

}
