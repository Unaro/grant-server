package com.grantserver.api.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.grantserver.common.auth.SessionManager;
import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.common.util.JsonUtils;
import com.grantserver.dto.request.GrantApplicationCreateDTO;
import com.grantserver.dto.response.ServerResponseDTO;
import com.grantserver.service.GrantApplicationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GrantApplicationsHandler implements HttpHandler {

    private final GrantApplicationService grantApplicationService;
    private final SessionManager sessionManager;

    public GrantApplicationsHandler() {
        this.grantApplicationService = ServiceRegistry.getInstance().get(GrantApplicationService.class);
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            // 1. ИЗВЛЕЧЕНИЕ ТОКЕНА (Для защищенных методов)
            Long userId = null;
            if (!"GET".equals(method)) { // Например, GET может быть публичным, но POST требует автора
                 userId = authenticate(exchange);
                 if (userId == null) {
                     sendResponse(exchange, 401, "{\"error\": \"Unauthorized\"}");
                     return;
                 }
            }

            // 2. РОУТИНГ
            if ("POST".equals(method) && path.equals("/api/applications")) {
                handleCreate(exchange, userId);
            } else if ("GET".equals(method) && path.equals("/api/applications")) {
                handleGetAll(exchange);
            } else {
                sendResponse(exchange, 404, "Not Found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Error: " + e.getMessage());
        }
    }

    // Хелпер для проверки токена
    private Long authenticate(HttpExchange exchange) {
        if (!exchange.getRequestHeaders().containsKey("Authorization")) {
            return null;
        }
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        // Ожидаем формат "Bearer <token>"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7); // Отрезаем "Bearer "
        return sessionManager.getUserId(token);
    }

    private void handleCreate(HttpExchange exchange, Long userId) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        GrantApplicationCreateDTO dto = JsonUtils.fromJson(body, GrantApplicationCreateDTO.class);

        // Передаем userId, полученный из токена!
        var result = grantApplicationService.create(dto, userId);

        String responseJson = JsonUtils.toJson(new ServerResponseDTO(200, result));
        sendResponse(exchange, 200, responseJson);
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        var result = grantApplicationService.getAll();
        String responseJson = JsonUtils.toJson(new ServerResponseDTO(200, result));
        sendResponse(exchange, 200, responseJson);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}