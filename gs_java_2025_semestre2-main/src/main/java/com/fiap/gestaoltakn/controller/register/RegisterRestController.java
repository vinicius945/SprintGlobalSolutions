package com.fiap.gestaoltakn.controller.register;

import com.fiap.gestaoltakn.dto.UserDTO;
import com.fiap.gestaoltakn.entity.UserEntity;
import com.fiap.gestaoltakn.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegisterRestController {

    private final UserService userService;

    public RegisterRestController(UserService userService) {

        this.userService = userService;

    }

    @PostMapping
    public ResponseEntity<UserEntity> register(@RequestBody UserDTO userDTO) {

        if (userService.existsByUsername(userDTO.getUsername())) {

            return ResponseEntity.badRequest().build();

        }

        UserEntity createdUser = userService.registerNewUser(userDTO);
        return ResponseEntity.ok(createdUser);

    }

}
