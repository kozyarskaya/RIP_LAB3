package com.example.quote_server.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class QuoteService {
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
