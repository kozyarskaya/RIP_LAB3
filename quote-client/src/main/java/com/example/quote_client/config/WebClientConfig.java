package com.example.quote_client.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    // Базовый URL нашего Сервиса B
    private static final String QUOTE_SERVER_URL = "http://localhost:8081";

    @Bean
    public WebClient quoteServiceWebClient() {

        // Настройка таймаутов на уровне TCP-клиента Reactor Netty
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000) // Таймаут на подключение 2 сек
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(3000, TimeUnit.MILLISECONDS)) // Таймаут на чтение 3 сек
                        .addHandlerLast(new WriteTimeoutHandler(3000, TimeUnit.MILLISECONDS))); // Таймаут на запись 3 сек

        return WebClient.builder()
                .baseUrl(QUOTE_SERVER_URL)
                // Настройка стратегии повторных попыток (retry) с экспоненциальным откатом (backoff)
                .filter((request, next) -> next.exchange(request)
                        .retryWhen(Retry.backoff(3, Duration.ofMillis(100)) // 3 попытки
                                .maxBackoff(Duration.ofSeconds(2)) // Макс. задержка 2 сек
                                .onRetryExhaustedThrow((spec, signal) -> {
                                    // Создаем своё исключение при исчерпании попыток
                                    throw new RuntimeException("Сервис цитат не отвежает после " + signal.totalRetries() + " попыток");
                                })))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}