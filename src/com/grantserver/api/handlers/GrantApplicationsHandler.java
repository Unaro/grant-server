package com.grantserver.api.handlers;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.common.util.JsonUtils;
import com.grantserver.dto.request.GrantApplicationCreateDTO;
import com.grantserver.dto.response.GrantApplicationDTO;
import com.grantserver.dto.response.ServerResponseDTO;
import com.grantserver.service.GrantApplicationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GrantApplicationsHandler implements HttpHandler {

    private final GrantApplicationService grantApplicationService;

    public GrantApplicationsHandler() {
        this.grantApplicationService = ServiceRegistry.getInstance().get(GrantApplicationService.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("POST".equals(method) && path.endsWith("/api/applications")) {
                handleCreate(exchange);
            } else if ("GET".equals(method) && path.endsWith("/api/applications")) {
                handleList(exchange);
            } else {
                sendResponse(exchange, 404, "Endpoint not found");
            }
        } catch (IOException e) {
            sendResponse(exchange, 400, "Error: " + e.getMessage());
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        // 1. Получаем ID пользователя из заголовка (имитация проверки токена)
        Long ownerId = getUserIdFromHeaders(exchange);
        if (ownerId == null) {
            sendResponse(exchange, 401, "Unauthorized: Missing X-User-Id header");
            return;
        }

        // 2. Читаем тело
        String body = readRequestBody(exchange.getRequestBody());
        if (body == null || body.trim().isEmpty()) {
            sendResponse(exchange, 400, "Request body is empty");
            return;
        }

        // 3. Парсим DTO
        GrantApplicationCreateDTO dto = JsonUtils.fromJson(body, GrantApplicationCreateDTO.class);
        if (dto == null) {
            sendResponse(exchange, 400, "Invalid JSON");
            return;
        }

        // 4. Создаем заявку
        GrantApplicationDTO created = grantApplicationService.create(dto, ownerId);
        
        sendResponse(exchange, 200, created);
    }

    private void handleList(HttpExchange exchange) throws IOException {
        // Простой GET без фильтров (фильтры добавим позже при необходимости)
        List<GrantApplicationDTO> list = grantApplicationService.getAll();
        sendResponse(exchange, 200, list);
    }

    // --- Утилиты ---

    private Long getUserIdFromHeaders(HttpExchange exchange) {
        // Временное решение для тестов: читаем ID напрямую из заголовка
        List<String> userIdHeaders = exchange.getRequestHeaders().get("X-User-Id");
        if (userIdHeaders != null && !userIdHeaders.isEmpty()) {
            try {
                return Long.valueOf(userIdHeaders.get(0));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        ServerResponseDTO responseDTO = new ServerResponseDTO(statusCode, data);
        String jsonResponse = JsonUtils.toJson(responseDTO);
        
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readRequestBody(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}