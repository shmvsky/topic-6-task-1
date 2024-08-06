package ru.shmvsky.urlcut.core.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.shmvsky.urlcut.core.service.UrlService;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/cut")
    public String shortenUrl(
            @RequestParam(name = "destination") String destination,
            @RequestParam(name = "alias", required = false) String alias,
            @RequestParam(name = "ttl", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ttl) {

        return urlService.shortenUrl(destination, alias, ttl);
    }

    @GetMapping("/{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        String destination = urlService.getDestination(shortUrl);
        response.sendRedirect(destination);
    }

}
