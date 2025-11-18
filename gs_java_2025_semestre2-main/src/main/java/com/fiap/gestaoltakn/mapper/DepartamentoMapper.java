package com.fiap.gestaoltakn.mapper;

import com.fiap.gestaoltakn.dto.DepartamentoDTO;
import com.fiap.gestaoltakn.entity.DepartamentoEntity;

public class DepartamentoMapper {

    public static DepartamentoDTO toDepartamentoDTO(DepartamentoEntity entity) {

        if (entity == null) return null;

        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setNumeroHorasMaximas(entity.getNumeroHorasMaximas());

        return dto;

    }

    public static DepartamentoEntity toDepartamentoEntity(DepartamentoDTO dto) {

        if (dto == null) return null;

        DepartamentoEntity entity = new DepartamentoEntity();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setNumeroHorasMaximas(dto.getNumeroHorasMaximas());

        return entity;

    }

}
