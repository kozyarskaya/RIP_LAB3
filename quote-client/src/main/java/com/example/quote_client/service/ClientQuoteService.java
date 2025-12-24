package com.example.quote_client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientQuoteService {

    private final WebClient quoteServiceWebClient;

    public Mono<String> fetchRandomQuote() {
        log.info("Клиент отправляет запрос к серверу цитат...");

        return quoteServiceWebClient.get()
                .uri("/api/quotes/random")
                .retrieve()
                .bodyToMono(String.class)
                // Обработка ошибок HTTP (4xx, 5xx)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    HttpStatus status = (HttpStatus) ex.getStatusCode();
                    log.error("HTTP ошибка от сервера: {} {}", status.value(), status.getReasonPhrase());
                    if (status.is5xxServerError()) {
                        return Mono.just("Сервер цитат временно недоступен. Попробуйте позже.");
                    } else {
                        return Mono.just("Запрос к серверу цитат выполнен с ошибкой: " + status.value());
                    }
                })
                // Обработка прочих ошибок (таймаут, сетевые проблемы)
                .onErrorResume(ex -> {
                    log.error("Ошибка при вызове сервиса цитат: ", ex);
                    return Mono.just("Не удалось получить цитату: " + ex.getMessage());
                })
                // Логирование успешного результата
                .doOnSuccess(quote -> log.info("Успешно получена цитата от сервера: {}", quote));
    }
}