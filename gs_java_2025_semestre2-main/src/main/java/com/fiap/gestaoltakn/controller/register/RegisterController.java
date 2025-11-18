package com.fiap.gestaoltakn.controller.register;

import com.fiap.gestaoltakn.dto.UserDTO;
import com.fiap.gestaoltakn.service.UserService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final UserService userService;
    private final MessageSource messageSource;

    public RegisterController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        if (userService.existsByUsername(userDTO.getUsername())) {
            result.rejectValue("username", "error.userDTO",
                    messageSource.getMessage("app.validation.username.exists", null, LocaleContextHolder.getLocale()));
            return "register";
        }

        if (userDTO.getRole() == null) {
            userDTO.setRole(com.fiap.gestaoltakn.enums.Role.USER);
        }

        userService.registerNewUser(userDTO);
        model.addAttribute("successMessage",
                messageSource.getMessage("app.register.success", null, LocaleContextHolder.getLocale()));

        return "login";
    }

}
