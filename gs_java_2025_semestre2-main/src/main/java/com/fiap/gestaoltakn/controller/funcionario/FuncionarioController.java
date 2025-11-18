package com.fiap.gestaoltakn.controller.funcionario;

import com.fiap.gestaoltakn.dto.FuncionarioDTO;
import com.fiap.gestaoltakn.entity.DepartamentoEntity;
import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import com.fiap.gestaoltakn.mapper.FuncionarioMapper;
import com.fiap.gestaoltakn.service.DepartamentoService;
import com.fiap.gestaoltakn.service.FuncionarioService;
import com.fiap.gestaoltakn.ai.service.AIService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;
    private final DepartamentoService departamentoService;
    private final AIService aiService;

    public FuncionarioController(FuncionarioService funcionarioService,
                                 DepartamentoService departamentoService,
                                 AIService aiService) {
        this.funcionarioService = funcionarioService;
        this.departamentoService = departamentoService;
        this.aiService = aiService;
    }

    @GetMapping
    public ResponseEntity<Page<FuncionarioDTO>> listar(Pageable pageable, @RequestParam(required = false) Long departamentoId) {
        Page<FuncionarioEntity> page = (departamentoId == null) ?
                funcionarioService.listar(pageable) :
                funcionarioService.listarPorDepartamento(departamentoId, pageable);

        Page<FuncionarioDTO> result = page.map(FuncionarioMapper::toFuncionarioDTO);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                .body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> buscar(@PathVariable Long id) {
        FuncionarioEntity encontrado = funcionarioService.buscarPorId(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
                .body(FuncionarioMapper.toFuncionarioDTO(encontrado));
    }

    @GetMapping("/{id}/analise-ia")
    public ResponseEntity<String> obterAnaliseIA(@PathVariable Long id) {
        try {
            FuncionarioEntity funcionario = funcionarioService.buscarPorId(id);
            String analise = aiService.analisarBemEstarFuncionario(funcionario);
            return ResponseEntity.ok(analise);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao gerar análise: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<FuncionarioDTO> criar(@Valid @RequestBody FuncionarioDTO dto) {
        DepartamentoEntity departamento = departamentoService.buscarPorId(dto.getDepartamentoId());
        FuncionarioEntity entity = FuncionarioMapper.toFuncionarioEntity(dto, departamento);
        FuncionarioEntity salvo = funcionarioService.criar(entity);
        return ResponseEntity.ok(FuncionarioMapper.toFuncionarioDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> atualizar(@PathVariable Long id, @Valid @RequestBody FuncionarioDTO dto) {
        DepartamentoEntity departamento = departamentoService.buscarPorId(dto.getDepartamentoId());
        FuncionarioEntity atualizado = FuncionarioMapper.toFuncionarioEntity(dto, departamento);
        FuncionarioEntity salvo = funcionarioService.atualizar(id, atualizado);
        return ResponseEntity.ok(FuncionarioMapper.toFuncionarioDTO(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
