package com.example.quote_server.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class QuoteService {

    private final ResourceLoader resourceLoader;

    public QuoteService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // Метод инициализации, запускается после создания бина.
    @PostConstruct
    public void init() {
        log.warn("Сервис цитат инициализирован.");
    }

    // Основной неоптимальный метод
    public Mono<String> getRandomQuote() {
        return Mono.fromCallable(() -> {
            long startTime = System.currentTimeMillis();

            // 1. Неоптимально: при КАЖДОМ запросе читаем весь файл с диска.
            Resource resource = resourceLoader.getResource("classpath:quotes.txt");
            Path filePath = Paths.get(resource.getURI());
            List<String> lines = Files.readAllLines(filePath); // O(n) по чтению

            // 2. Неоптимально: используем Collections.shuffle для всей коллекции O(n),
            // хотя для выбора одного элемента достаточно random.
            Collections.shuffle(lines); // O(n)

            String chosenQuote = lines.isEmpty() ? "Цитаты не найдены." : lines.get(0);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Неоптимальный метод выполнился за {} мс. Выбрана цитата: {}", duration, chosenQuote);

            return chosenQuote;
        });
    }
}