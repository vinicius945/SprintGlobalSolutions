package com.fiap.gestaoltakn.repository;

import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import com.fiap.gestaoltakn.enums.FuncionarioStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuncionarioRepository extends JpaRepository<FuncionarioEntity, Long> {

    List<FuncionarioEntity> findByNomeContainingIgnoreCase(String nome);

    List<FuncionarioEntity> findByDepartamentoNomeContainingIgnoreCase(String departamentoNome);

    List<FuncionarioEntity> findByHorasTrabalhadasUltimoMes(Integer horas);

    List<FuncionarioEntity> findByStatus(FuncionarioStatus status);

    Page<FuncionarioEntity> findByDepartamento_Id(Long departamentoId, Pageable pageable);

}
