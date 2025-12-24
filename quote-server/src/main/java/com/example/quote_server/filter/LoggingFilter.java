package com.example.quote_server.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1)
@Slf4j
public class LoggingFilter implements WebFilter { // Используем WebFilter

@Override
public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        
        long startTime = System.currentTimeMillis();
        
        // Логируем входящий запрос
        String requestInfo = String.format("ВХОДЯЩИЙ | Method: %s | URI: %s",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath());
        log.info(requestInfo);
        
        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    // Логируем исходящий ответ
                        long duration = System.currentTimeMillis() - startTime;
                        String responseInfo = String.format("ИСХОДЯЩИЙ | Status: %d | Time: %dms | URI: %s",
                                exchange.getResponse().getStatusCode() != null 
                                ? exchange.getResponse().getStatusCode().value() 
                                : 0,
                                duration,
                                exchange.getRequest().getURI().getPath());
                        log.info(responseInfo);
                });
        }
}