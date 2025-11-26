package com.grantserver;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.ParticipantDAO;
import com.grantserver.dao.impl.ParticipantDAOImpl;

public class Main {
    
    // Порт по умолчанию
    private static final int PORT = 8080; 

    public static void main(String[] args) throws IOException {
        System.out.println("Инициализация GrantServer...");

        // 1. Инициализация зависимостей (Bootstrap)
        // Здесь мы будем создавать DAO и Сервисы через Factory и класть в Registry
        // initializeContext(); 

        // 2. Запуск HTTP сервера
        // Используем встроенный HttpServer (JDK 6+)
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // 3. Создание контекстов (роутинг)
        // server.createContext("/api/participants", new ParticipantsHandler());
        // server.createContext("/api/experts", new ExpertsHandler());
        
        // Базовый эндпоинт для проверки жизни
        server.createContext("/api/health", exchange -> {
            String response = "{\"status\": \"OK\", \"server\": \"GrantServer v1.0\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        // 4. Настройка пула потоков
        // Создаем pool потоков для обработки запросов (похоже на Tomcat)
        server.setExecutor(Executors.newFixedThreadPool(10)); 

        server.start();
        System.out.println("Сервер запущен на порту: " + PORT);
        System.out.println("Доступен по адресу: http://localhost:" + PORT + "/api/health");
    }

    private static void initializeContext() {
        ServiceRegistry registry = ServiceRegistry.getInstance();
        
        // 1. Создаем DAO
        ParticipantDAO participantDAO = new ParticipantDAOImpl();
        
        // 2. Регистрируем в контейнере
        registry.register(ParticipantDAO.class, participantDAO);
        
        System.out.println("Контекст инициализирован: DAO зарегистрированы.");
    }
}