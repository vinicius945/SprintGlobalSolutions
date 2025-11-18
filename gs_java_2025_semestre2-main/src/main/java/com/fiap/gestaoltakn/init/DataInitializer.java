package com.fiap.gestaoltakn.init;

import com.fiap.gestaoltakn.entity.UserEntity;
import com.fiap.gestaoltakn.enums.Role;
import com.fiap.gestaoltakn.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test") // Não executa durante os testes
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.findByUsername("user").isEmpty()) {
            userRepository.save(UserEntity.builder()
                    .username("user")
                    .password(passwordEncoder.encode("userpass"))
                    .role(Role.USER)
                    .build());
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(UserEntity.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("adminpass"))
                    .role(Role.ADMIN)
                    .build());
        }

    }

}
