package com.fiap.gestaoltakn.mapper;

import com.fiap.gestaoltakn.dto.FuncionarioDTO;
import com.fiap.gestaoltakn.entity.DepartamentoEntity;
import com.fiap.gestaoltakn.entity.FuncionarioEntity;

public class FuncionarioMapper {

    public static FuncionarioDTO toFuncionarioDTO(FuncionarioEntity entity) {

        if (entity == null) return null;

        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        if (entity.getDepartamento() != null) {
            dto.setDepartamentoId(entity.getDepartamento().getId());
            dto.setDepartamentoNome(entity.getDepartamento().getNome());
        }
        dto.setHorasTrabalhadasUltimoMes(entity.getHorasTrabalhadasUltimoMes());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    public static FuncionarioEntity toFuncionarioEntity(FuncionarioDTO dto, DepartamentoEntity departamento) {

        if (dto == null) return null;

        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setDepartamento(departamento);
        entity.setHorasTrabalhadasUltimoMes(dto.getHorasTrabalhadasUltimoMes());
        return entity;
    }

}
