package com.fiap.gestaoltakn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GS_TB_DEPARTAMENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "O número de horas máximas é obrigatório")
    private Integer numeroHorasMaximas;

    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FuncionarioEntity> funcionarios = new ArrayList<>();

}
