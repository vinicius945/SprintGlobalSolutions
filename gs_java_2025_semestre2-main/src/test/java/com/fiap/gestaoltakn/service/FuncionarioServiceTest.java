package com.fiap.gestaoltakn.service;

import com.fiap.gestaoltakn.entity.DepartamentoEntity;
import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import com.fiap.gestaoltakn.enums.FuncionarioStatus;
import com.fiap.gestaoltakn.repository.FuncionarioRepository;
import com.fiap.gestaoltakn.service.message.CacheSyncProducer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private CacheSyncProducer cacheSyncProducer;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private FuncionarioEntity funcionario;
    private DepartamentoEntity departamento;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        departamento = new DepartamentoEntity();
        departamento.setId(1L);
        departamento.setNome("Recursos Humanos");
        departamento.setNumeroHorasMaximas(200);

        funcionario = new FuncionarioEntity();
        funcionario.setId(1L);
        funcionario.setNome("João Silva");
        funcionario.setDepartamento(departamento);
        funcionario.setHorasTrabalhadasUltimoMes(160);
        funcionario.setStatus(FuncionarioStatus.SAUDAVEL);
    }

    @Test
    void deveListarFuncionariosSemFiltro() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<FuncionarioEntity> page = new PageImpl<>(Collections.singletonList(funcionario));
        when(funcionarioRepository.findAll(pageable)).thenReturn(page);

        Page<FuncionarioEntity> resultado = funcionarioService.listar(pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(funcionarioRepository, times(1)).findAll(pageable);
    }

    @Test
    void deveListarTodosFuncionarios() {
        when(funcionarioRepository.findAll()).thenReturn(Collections.singletonList(funcionario));

        List<FuncionarioEntity> resultado = funcionarioService.listarTodos();

        assertEquals(1, resultado.size());
        verify(funcionarioRepository, times(1)).findAll();
    }

    @Test
    void deveListarFuncionariosPorDepartamento() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<FuncionarioEntity> page = new PageImpl<>(Collections.singletonList(funcionario));
        when(funcionarioRepository.findByDepartamento_Id(1L, pageable)).thenReturn(page);

        Page<FuncionarioEntity> resultado = funcionarioService.listarPorDepartamento(1L, pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(funcionarioRepository, times(1)).findByDepartamento_Id(1L, pageable);
    }

    @Test
    void deveBuscarFuncionarioPorId() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));

        FuncionarioEntity encontrado = funcionarioService.buscarPorId(1L);

        assertNotNull(encontrado);
        assertEquals(160, encontrado.getHorasTrabalhadasUltimoMes());
        verify(funcionarioRepository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarFuncionarioInexistente() {
        when(funcionarioRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> funcionarioService.buscarPorId(2L));
        verify(funcionarioRepository, times(1)).findById(2L);
    }

    @Test
    void deveCriarFuncionario() {
        when(funcionarioRepository.save(any(FuncionarioEntity.class))).thenReturn(funcionario);

        FuncionarioEntity criado = funcionarioService.criar(funcionario);

        assertNotNull(criado);
        assertEquals(FuncionarioStatus.SAUDAVEL, criado.getStatus());
        verify(funcionarioRepository, times(1)).save(funcionario);
        verify(cacheSyncProducer, times(1)).enviarInvalidacaoFuncionario(1L);
    }

    @Test
    void deveAtualizarFuncionario() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any(FuncionarioEntity.class))).thenAnswer(invocation -> {
            FuncionarioEntity func = invocation.getArgument(0);
            return func;
        });

        FuncionarioEntity atualizado = new FuncionarioEntity();
        atualizado.setNome("Maria Santos");
        atualizado.setDepartamento(departamento);
        atualizado.setHorasTrabalhadasUltimoMes(210);

        FuncionarioEntity resultado = funcionarioService.atualizar(1L, atualizado);

        assertEquals(210, resultado.getHorasTrabalhadasUltimoMes());
        assertEquals(FuncionarioStatus.EM_RISCO, resultado.getStatus());
        verify(funcionarioRepository, times(1)).save(any(FuncionarioEntity.class));
        verify(cacheSyncProducer, times(1)).enviarInvalidacaoFuncionario(1L);
    }

    @Test
    void deveDeletarFuncionario() {
        doNothing().when(funcionarioRepository).deleteById(1L);

        funcionarioService.deletar(1L);

        verify(funcionarioRepository, times(1)).deleteById(1L);
        verify(cacheSyncProducer, times(1)).enviarInvalidacaoFuncionario(1L);
    }

    @Test
    void computeStatus_ComHorasDentroDoLimite_DeveRetornarSaudavel() {
        funcionario.setHorasTrabalhadasUltimoMes(150); // Dentro do limite de 200

        FuncionarioStatus status = funcionarioService.computeStatus(funcionario);

        assertEquals(FuncionarioStatus.SAUDAVEL, status);
    }

    @Test
    void computeStatus_ComHorasAcimaDoLimite_DeveRetornarEmRisco() {
        funcionario.setHorasTrabalhadasUltimoMes(210); // Acima do limite de 200

        FuncionarioStatus status = funcionarioService.computeStatus(funcionario);

        assertEquals(FuncionarioStatus.EM_RISCO, status);
    }

    @Test
    void computeStatus_ComDepartamentoNulo_DeveRetornarSaudavel() {
        funcionario.setDepartamento(null);
        funcionario.setHorasTrabalhadasUltimoMes(300);

        FuncionarioStatus status = funcionarioService.computeStatus(funcionario);

        assertEquals(FuncionarioStatus.SAUDAVEL, status);
    }

}
