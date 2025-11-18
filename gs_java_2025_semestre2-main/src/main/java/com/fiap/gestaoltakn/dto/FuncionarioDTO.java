package com.fiap.gestaoltakn.dto;

import com.fiap.gestaoltakn.enums.FuncionarioStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FuncionarioDTO {

    private Long id;

    @NotBlank
    private String nome;

    @NotNull
    private Long departamentoId;

    private String departamentoNome;

    @NotNull
    private Integer horasTrabalhadasUltimoMes;

    private FuncionarioStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getDepartamentoNome() {
        return departamentoNome;
    }

    public void setDepartamentoNome(String departamentoNome) {
        this.departamentoNome = departamentoNome;
    }

    public Integer getHorasTrabalhadasUltimoMes() {
        return horasTrabalhadasUltimoMes;
    }

    public void setHorasTrabalhadasUltimoMes(Integer horasTrabalhadasUltimoMes) {
        this.horasTrabalhadasUltimoMes = horasTrabalhadasUltimoMes;
    }

    public com.fiap.gestaoltakn.enums.FuncionarioStatus getStatus() {
        return status;
    }

    public void setStatus(com.fiap.gestaoltakn.enums.FuncionarioStatus status) {
        this.status = status;
    }

}
