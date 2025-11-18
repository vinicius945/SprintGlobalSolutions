package com.fiap.gestaoltakn.entity;

import com.fiap.gestaoltakn.enums.FuncionarioStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "GS_TB_FUNCIONARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuncionarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "DEPARTAMENTO_ID", nullable = false)
    private DepartamentoEntity departamento;

    @NotNull(message = "Horas trabalhadas no último mês são obrigatórias")
    private Integer horasTrabalhadasUltimoMes;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    private FuncionarioStatus status;

}
