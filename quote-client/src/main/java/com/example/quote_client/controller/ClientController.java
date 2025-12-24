package com.example.quote_client.controller;

import com.example.quote_client.service.ClientQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientQuoteService clientQuoteService;

    @GetMapping("/quote")
    public Mono<String> getQuoteFromServer() {
        // Здесь WebClient работает асинхронно, но контроллер возвращает реактивную обертку
        return clientQuoteService.fetchRandomQuote();
    }

    @GetMapping("/quote-block")
    public String getQuoteFromServerBlocking() {
        // Пример СИНХРОННОГО вызова через block() для демонстрации [citation:1][citation:10]
        // В реальном реактивном приложении так делать не рекомендуется
        return clientQuoteService.fetchRandomQuote().block();
    }
}