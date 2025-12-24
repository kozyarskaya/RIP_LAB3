package com.example.quote_server.controller;

import com.example.quote_server.service.QuoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
@Slf4j
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping(value = "/random", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> getRandomQuote() {
        log.info("Получен запрос на случайную цитату");
        return quoteService.getRandomQuote()
                .onErrorResume(e -> {
                    log.error("Ошибка при получении цитаты: ", e);
                    return Mono.just("Извините, не удалось загрузить цитату. Ошибка сервера.");
                });
    }

    // НОВЫЙ ЭНДПОИНТ с операторами
    @GetMapping(value = "/random-with-operators", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> getRandomQuoteWithOperators() {
        log.info("Запрос на цитату с операторами Flux");
        
        return quoteService.getRandomQuoteWithOperators()
            // Дополнительные операторы на уровне контроллера
            .map(result -> "[" + System.currentTimeMillis() + "] " + result)
            .doOnSuccess(result -> 
                log.info("Успешно возвращена цитата с операторами: {}", result.substring(0, Math.min(50, result.length())) + "..."))
            .onErrorResume(e -> {
                log.error("Ошибка в контроллере: ", e);
                return Mono.just("Контроллер: ошибка обработки запроса");
            });
    }
    
    // Эндпоинт для тестирования Flux напрямую
    @GetMapping(value = "/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAllQuotesStream() {
        log.info("Запрос на поток всех цитат");
        
        return quoteService.getRandomQuoteWithOperators()
            .flatMapMany(quote -> 
                Flux.just("Первая: " + quote, "Вторая: " + quote.toUpperCase(), "Третья: " + quote.toLowerCase()))
            .delayElements(java.time.Duration.ofMillis(100)) // Имитация задержки
            .take(5); // Ограничиваем 5 элементами
    }
}