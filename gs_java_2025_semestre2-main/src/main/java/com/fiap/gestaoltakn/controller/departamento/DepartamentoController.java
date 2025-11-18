package com.fiap.gestaoltakn.controller.departamento;

import com.fiap.gestaoltakn.dto.DepartamentoDTO;
import com.fiap.gestaoltakn.entity.DepartamentoEntity;
import com.fiap.gestaoltakn.mapper.DepartamentoMapper;
import com.fiap.gestaoltakn.service.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoDTO>> listar() {
        List<DepartamentoDTO> departamentos = departamentoService.listarTodos().stream()
                .map(DepartamentoMapper::toDepartamentoDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
                .body(departamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> buscar(@PathVariable Long id) {
        DepartamentoEntity encontrado = departamentoService.buscarPorId(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
                .body(DepartamentoMapper.toDepartamentoDTO(encontrado));
    }

    @PostMapping
    public ResponseEntity<DepartamentoDTO> criar(@Valid @RequestBody DepartamentoDTO dto) {
        DepartamentoEntity entity = DepartamentoMapper.toDepartamentoEntity(dto);
        DepartamentoEntity salvo = departamentoService.criar(entity);
        return ResponseEntity.ok(DepartamentoMapper.toDepartamentoDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody DepartamentoDTO dto) {
        DepartamentoEntity atualizado = DepartamentoMapper.toDepartamentoEntity(dto);
        DepartamentoEntity salvo = departamentoService.atualizar(id, atualizado);
        return ResponseEntity.ok(DepartamentoMapper.toDepartamentoDTO(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        departamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
