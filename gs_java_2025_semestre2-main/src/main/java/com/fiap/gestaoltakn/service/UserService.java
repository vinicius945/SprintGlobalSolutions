package com.fiap.gestaoltakn.service;

import com.fiap.gestaoltakn.dto.UserDTO;
import com.fiap.gestaoltakn.entity.UserEntity;
import com.fiap.gestaoltakn.enums.Role;
import com.fiap.gestaoltakn.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public boolean existsByUsername(String username) {

        return userRepository.findByUsername(username).isPresent();

    }

    public UserEntity registerNewUser(UserDTO userDTO) {
        Role role = (userDTO.getRole() != null) ? userDTO.getRole() : Role.USER;

        UserEntity user = UserEntity.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .role(role)
                .build();

        return userRepository.save(user);
    }

}
