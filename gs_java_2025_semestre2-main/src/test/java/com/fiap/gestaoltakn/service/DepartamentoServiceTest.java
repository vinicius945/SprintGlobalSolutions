package com.fiap.gestaoltakn.service;

import com.fiap.gestaoltakn.entity.DepartamentoEntity;
import com.fiap.gestaoltakn.repository.DepartamentoRepository;
import com.fiap.gestaoltakn.service.message.CacheSyncProducer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartamentoServiceTest {

    @Mock
    private DepartamentoRepository departamentoRepository;

    @Mock
    private CacheSyncProducer cacheSyncProducer;

    @InjectMocks
    private DepartamentoService departamentoService;

    private DepartamentoEntity departamento;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        departamento = new DepartamentoEntity();
        departamento.setId(1L);
        departamento.setNome("Financeiro");
        departamento.setNumeroHorasMaximas(180);
    }

    @Test
    void deveListarTodosOsDepartamentos() {
        when(departamentoRepository.findAll()).thenReturn(Arrays.asList(departamento));

        List<DepartamentoEntity> lista = departamentoService.listarTodos();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Financeiro", lista.get(0).getNome());
        verify(departamentoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarDepartamentoPorId() {
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(departamento));

        DepartamentoEntity encontrado = departamentoService.buscarPorId(1L);

        assertNotNull(encontrado);
        assertEquals("Financeiro", encontrado.getNome());
        assertEquals(180, encontrado.getNumeroHorasMaximas());
        verify(departamentoRepository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarDepartamentoInexistente() {
        when(departamentoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> departamentoService.buscarPorId(2L));
        verify(departamentoRepository, times(1)).findById(2L);
    }

    @Test
    void deveCriarDepartamento() {
        when(departamentoRepository.save(any(DepartamentoEntity.class))).thenReturn(departamento);

        DepartamentoEntity criado = departamentoService.criar(departamento);

        assertNotNull(criado);
        assertEquals("Financeiro", criado.getNome());
        assertEquals(180, criado.getNumeroHorasMaximas());
        verify(departamentoRepository, times(1)).save(departamento);
        verify(cacheSyncProducer, times(1)).enviarInvalidacaoDepartamento(1L);
    }

    @Test
    void deveAtualizarDepartamento() {
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(departamento));
        when(departamentoRepository.save(any(DepartamentoEntity.class))).thenReturn(departamento);

        DepartamentoEntity atualizado = new DepartamentoEntity();
        atualizado.setNome("Jurídico");
        atualizado.setNumeroHorasMaximas(160);

        DepartamentoEntity resultado = departamentoService.atualizar(1L, atualizado);

        assertEquals("Jurídico", resultado.getNome());
        assertEquals(160, resultado.getNumeroHorasMaximas());
        verify(departamentoRepository, times(1)).save(departamento);
        verify(cacheSyncProducer, times(1)).enviarInvalidacaoDepartamento(1L);
    }

    @Test
    void deveDeletarDepartamento() {
        doNothing().when(departamentoRepository).deleteById(1L);

        departamentoService.deletar(1L);

        verify(departamentoRepository, times(1)).deleteById(1L);
        verify(cacheSyncProducer, times(1)).enviarInvalidacaoDepartamento(1L);
    }

}
