package com.fiap.gestaoltakn.repository;

import com.fiap.gestaoltakn.entity.DepartamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartamentoRepository extends JpaRepository<DepartamentoEntity, Long> {

    List<DepartamentoEntity> findByNomeContainingIgnoreCase(String nome);

    List<DepartamentoEntity> findByNumeroHorasMaximas(Integer numeroHorasMaximas);

}
