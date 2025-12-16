package com.grantserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.grantserver.api.handlers.EvaluationsHandler;
import com.grantserver.api.handlers.ExpertsHandler;
import com.grantserver.api.handlers.GrantApplicationsHandler;
import com.grantserver.api.handlers.GrantFundHandler;
import com.grantserver.api.handlers.ParticipantsHandler;
import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.EvaluationDAO;
import com.grantserver.dao.ExpertDAO;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.dao.ParticipantDAO;
import com.grantserver.dao.impl.EvaluationDAOImpl;
import com.grantserver.dao.impl.ExpertDAOImpl;
import com.grantserver.dao.impl.GrantApplicationDAOImpl;
import com.grantserver.dao.impl.ParticipantDAOImpl;
import com.grantserver.service.EvaluationService;
import com.grantserver.service.ExpertService;
import com.grantserver.service.GrantApplicationService;
import com.grantserver.service.GrantFundService;
import com.grantserver.service.ParticipantService;
import com.grantserver.service.impl.EvaluationServiceImpl;
import com.grantserver.service.impl.ExpertServiceImpl;
import com.grantserver.service.impl.GrantApplicationServiceImpl;
import com.grantserver.service.impl.GrantFundServiceImpl;
import com.grantserver.service.impl.ParticipantServiceImpl;
import com.sun.net.httpserver.HttpServer;

public class Main {
    
    private static final int PORT = 8080; 

    public static void main(String[] args) throws IOException {
        startServer();
        System.out.println("Сервер запущен на порту: " + PORT);
    }

    public static HttpServer startServer() throws IOException {
        System.out.println("Инициализация GrantServer...");
        initializeContext(); 

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/health", exchange -> {
            String response = "{\"status\": \"OK\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        server.createContext("/api/participants", new ParticipantsHandler());
        server.createContext("/api/applications", new GrantApplicationsHandler());
        server.createContext("/api/experts", new ExpertsHandler());
        server.createContext("/api/evaluations", new EvaluationsHandler());
        server.createContext("/api/grants/compute", new GrantFundHandler());

        server.setExecutor(Executors.newFixedThreadPool(10)); 
        server.start();
        
        return server;
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

        EvaluationService evaluationService = new EvaluationServiceImpl();
        registry.register(EvaluationService.class, evaluationService);

        // --- 5. Grant Fund Module ---
        // Зависит от ApplicationDAO и EvaluationDAO
        GrantFundService grantFundService = new GrantFundServiceImpl();
        registry.register(GrantFundService.class, grantFundService);
        
        System.out.println("Контекст инициализирован: Все DAO и Services готовы.");
    }
}