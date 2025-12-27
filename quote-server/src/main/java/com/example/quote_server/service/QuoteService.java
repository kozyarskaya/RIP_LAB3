package com.example.quote_server.service;

<<<<<<< Updated upstream
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

=======
>>>>>>> Stashed changes
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
<<<<<<< Updated upstream
import java.util.Collections;
import java.util.List;
=======
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
>>>>>>> Stashed changes

@Service
@Slf4j
public class QuoteService {
<<<<<<< Updated upstream

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
=======
    private static final String QUOTES_FILE = "src/main/resources/quotes.txt";
    
    // Кэшированный список цитат (загружается один раз при старте)
    private List<String> quotes;
    
    // Общий генератор случайных чисел (не создаем при каждом запросе)
    private final Random random = new Random();

    // Метод инициализации - выполняется один раз при создании бина
    @PostConstruct
    public void init() {
        System.out.println("=== INIT CALLED (чтение файла) ===");  // Отладочный вывод
        try {
            // 1. Читаем файл ОДИН РАЗ при старте приложения
            quotes = Files.readAllLines(Paths.get(QUOTES_FILE));
            System.out.println("Загружено цитат: " + quotes.size());  // Отладочный вывод
        } catch (Exception e) {
            // 2. Фолбэк на случай ошибки чтения файла
            System.out.println("ОШИБКА чтения файла: " + e.getMessage());  // Отладочный вывод
            quotes = List.of("Ошибка чтения файла.");
        }
>>>>>>> Stashed changes
    }

    public String getRandomQuote() {
        System.out.println("getRandomQuote вызван, кэш: " + quotes.size());  // Отладочный вывод
        // 3. Проверяем, есть ли цитаты
        if (quotes.isEmpty()) {
            return "Нет цитат.";
        }
        
        // 4. Выбираем случайную цитату за O(1) без сортировки и shuffle
        return quotes.get(random.nextInt(quotes.size()));
    }
}