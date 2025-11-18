package com.fiap.gestaoltakn.entity;

import com.fiap.gestaoltakn.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GS_TB_USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

}
