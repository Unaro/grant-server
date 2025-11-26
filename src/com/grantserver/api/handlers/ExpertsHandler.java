package com.grantserver.api.handlers;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.common.util.JsonUtils;
import com.grantserver.dto.request.AuthRequestDTO;
import com.grantserver.dto.request.ExpertRegisterDTO;
import com.grantserver.dto.response.AuthResponseDTO;
import com.grantserver.dto.response.ExpertDTO;
import com.grantserver.dto.response.ServerResponseDTO;
import com.grantserver.service.ExpertService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ExpertsHandler implements HttpHandler {

    private final ExpertService expertService;

    public ExpertsHandler() {
        this.expertService = ServiceRegistry.getInstance().get(ExpertService.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            // Роутинг
            if ("POST".equals(method) && path.endsWith("/register")) {
                handleRegister(exchange);
            } else if ("POST".equals(method) && path.endsWith("/login")) {
                handleLogin(exchange);
            } else {
                sendResponse(exchange, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Error: " + e.getMessage());
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());
        
        if (body == null || body.trim().isEmpty()) {
            sendResponse(exchange, 400, "Request body is empty");
            return;
        }

        ExpertRegisterDTO dto = JsonUtils.fromJson(body, ExpertRegisterDTO.class);
        
        if (dto == null) {
            sendResponse(exchange, 400, "Invalid JSON");
            return;
        }

        ExpertDTO created = expertService.register(dto);
        sendResponse(exchange, 200, created);
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());
        
        if (body == null || body.trim().isEmpty()) {
            sendResponse(exchange, 400, "Request body is empty");
            return;
        }

        AuthRequestDTO dto = JsonUtils.fromJson(body, AuthRequestDTO.class);
        
        if (dto == null) {
            sendResponse(exchange, 400, "Invalid JSON");
            return;
        }

        AuthResponseDTO response = expertService.login(dto);
        sendResponse(exchange, 200, response);
    }

    // --- Утилиты ---

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