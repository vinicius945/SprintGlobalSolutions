package com.fiap.gestaoltakn.controller;

import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/i18n")
public class I18nTestController {

    private final MessageSource messageSource;

    public I18nTestController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test(@RequestParam(required = false) String lang) {
        Locale locale = LocaleContextHolder.getLocale();
        String localeString = locale.toString();
        String displayName = locale.getDisplayName(locale);
        String exampleMessage = messageSource.getMessage("app.test.message", null, "MISSING_KEY", locale);
        Map<String, String> result = new HashMap<>();
        result.put("locale", localeString);
        result.put("displayName", displayName);
        result.put("exampleMessage", exampleMessage);
        return ResponseEntity.ok(result);
    }

}
