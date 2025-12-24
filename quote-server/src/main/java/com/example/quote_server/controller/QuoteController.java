package com.example.quote_server.controller;

import com.example.quote_server.service.QuoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
@Slf4j
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping(value = "/random", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> getRandomQuote() {
        log.info("Получен запрос на случайную цитату");
        // Обработка ошибок на уровне контроллера
        return quoteService.getRandomQuote()
                .onErrorResume(e -> {
                    log.error("Ошибка при получении цитаты: ", e);
                    return Mono.just("Извините, не удалось загрузить цитату. Ошибка сервера.");
                });
    }
}