package com.fiap.gestaoltakn.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
public class LocaleController {

    private static final String COOKIE_NAME = "LT_AKN_LOCALE";
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30 dias

    @GetMapping("/set-locale")
    public String setLocale(@RequestParam("lang") String lang,
                            HttpServletRequest request,
                            HttpServletResponse response) {

        String value;
        if ("pt".equalsIgnoreCase(lang) || "pt_BR".equalsIgnoreCase(lang)) {
            value = "pt_BR";
        } else {
            value = "en";
        }

        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);

        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "redirect:/";
        }

        try {
            URI uri = new URI(referer);
            String path = uri.getRawPath() == null ? "/" : uri.getRawPath();
            String query = uri.getRawQuery();

            String newQuery = null;
            if (query != null && !query.isBlank()) {
                newQuery = Arrays.stream(query.split("&"))
                        .map(s -> s.split("=", 2))
                        .filter(parts -> parts.length == 0 || !parts[0].equalsIgnoreCase("lang"))
                        .map(parts -> parts.length == 2 ? parts[0] + "=" + parts[1] : parts[0])
                        .collect(Collectors.joining("&"));
                if (newQuery.isBlank()) newQuery = null;
            }

            String target = path + (newQuery != null ? ("?" + newQuery) : "");
            return "redirect:" + target;
        } catch (URISyntaxException e) {
            return "redirect:/";
        }
    }

}
