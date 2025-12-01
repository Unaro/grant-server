package com.grantserver;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

// Импорты инфраструктуры
import com.grantserver.common.config.ServiceRegistry;

// Импорты DAO
import com.grantserver.dao.ParticipantDAO;
import com.grantserver.dao.impl.ParticipantDAOImpl;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.dao.impl.GrantApplicationDAOImpl;
import com.grantserver.dao.ExpertDAO;
import com.grantserver.dao.impl.ExpertDAOImpl;
import com.grantserver.dao.EvaluationDAO;
import com.grantserver.dao.impl.EvaluationDAOImpl;

// Импорты Сервисов
import com.grantserver.service.ParticipantService;
import com.grantserver.service.impl.ParticipantServiceImpl;
import com.grantserver.service.GrantApplicationService;
import com.grantserver.service.impl.GrantApplicationServiceImpl;
import com.grantserver.service.ExpertService;
import com.grantserver.service.impl.ExpertServiceImpl;
import com.grantserver.service.EvaluationService;
import com.grantserver.service.impl.EvaluationServiceImpl;
import com.grantserver.service.GrantFundService;
import com.grantserver.service.impl.GrantFundServiceImpl;

// Импорты API Handlers
import com.grantserver.api.handlers.ParticipantsHandler;
import com.grantserver.api.handlers.GrantApplicationsHandler;
import com.grantserver.api.handlers.ExpertsHandler;
import com.grantserver.api.handlers.EvaluationsHandler;
import com.grantserver.api.handlers.GrantFundHandler;

public class Main {
    
    private static final int PORT = 8080; 

    public static void main(String[] args) throws IOException {
        System.out.println("Инициализация GrantServer...");

        // 1. Инициализация зависимостей (Bootstrap)
        initializeContext(); 

        // 2. Запуск HTTP сервера
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // 3. Создание контекстов (роутинг)
        
        // Health check
        server.createContext("/api/health", exchange -> {
            String response = "{\"status\": \"OK\", \"server\": \"GrantServer v1.0\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        // Подключаем все Handler'ы
        server.createContext("/api/participants", new ParticipantsHandler());
        server.createContext("/api/applications", new GrantApplicationsHandler());
        server.createContext("/api/experts", new ExpertsHandler());
        server.createContext("/api/evaluations", new EvaluationsHandler());
        server.createContext("/api/grants/compute", new GrantFundHandler());

        // 4. Настройка пула потоков
        server.setExecutor(Executors.newFixedThreadPool(10)); 

        server.start();
        System.out.println("Сервер запущен на порту: " + PORT);
        System.out.println("Доступен по адресу: http://localhost:" + PORT + "/api/health");
    }

    private static void initializeContext() {
        ServiceRegistry registry = ServiceRegistry.getInstance();
        
        // --- 1. Participants Module ---
        ParticipantDAO participantDAO = new ParticipantDAOImpl();
        registry.register(ParticipantDAO.class, participantDAO);
        
        ParticipantService participantService = new ParticipantServiceImpl();
        registry.register(ParticipantService.class, participantService);

        // --- 2. Grant Applications Module ---
        GrantApplicationDAO grantApplicationDAO = new GrantApplicationDAOImpl();
        registry.register(GrantApplicationDAO.class, grantApplicationDAO);

        GrantApplicationService grantApplicationService = new GrantApplicationServiceImpl();
        registry.register(GrantApplicationService.class, grantApplicationService);

        // --- 3. Experts Module ---
        ExpertDAO expertDAO = new ExpertDAOImpl();
        registry.register(ExpertDAO.class, expertDAO);

        ExpertService expertService = new ExpertServiceImpl();
        registry.register(ExpertService.class, expertService);

        // --- 4. Evaluations Module ---
        EvaluationDAO evaluationDAO = new EvaluationDAOImpl();
        registry.register(EvaluationDAO.class, evaluationDAO);

        // Важно: EvaluationService зависит от DAO экспертов и заявок, они должны быть выше
        EvaluationService evaluationService = new EvaluationServiceImpl();
        registry.register(EvaluationService.class, evaluationService);

        // --- 5. Grant Fund Module ---
        // Зависит от ApplicationDAO и EvaluationDAO
        GrantFundService grantFundService = new GrantFundServiceImpl();
        registry.register(GrantFundService.class, grantFundService);
        
        System.out.println("Контекст инициализирован: Все DAO и Services готовы.");
    }
}