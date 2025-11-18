package com.fiap.gestaoltakn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DepartamentoDTO {

    private Long id;

    @NotBlank
    private String nome;

    @NotNull
    private Integer numeroHorasMaximas;

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

    public Integer getNumeroHorasMaximas() {
        return numeroHorasMaximas;
    }

    public void setNumeroHorasMaximas(Integer numeroHorasMaximas) {
        this.numeroHorasMaximas = numeroHorasMaximas;
    }

}
