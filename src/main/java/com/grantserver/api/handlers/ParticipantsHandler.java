package com.grantserver.api.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.common.util.JsonUtils;
import com.grantserver.dto.request.AuthRequestDTO;
import com.grantserver.dto.request.ParticipantRegisterDTO;
import com.grantserver.dto.response.AuthResponseDTO;
import com.grantserver.dto.response.ServerResponseDTO;
import com.grantserver.service.ParticipantService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ParticipantsHandler implements HttpHandler {

    private final ParticipantService participantService;

    public ParticipantsHandler() {
        this.participantService = ServiceRegistry.getInstance().get(ParticipantService.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("POST".equals(method) && path.endsWith("/register")) {
                handleRegister(exchange);
            } else if ("POST".equals(method) && path.endsWith("/login")) {
                handleLogin(exchange);
            } else {
                sendResponse(exchange, 404, "Not Found");
            }
        } catch (Exception e) {
            // ГЛОБАЛЬНЫЙ ОТЛОВ ОШИБОК
            // Если сервис кинул ошибку - отправляем 400 Bad Request
            e.printStackTrace(); // Полезно видеть в консоли
            sendResponse(exchange, 400, e.getMessage());
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        ParticipantRegisterDTO dto = JsonUtils.fromJson(body, ParticipantRegisterDTO.class);

        // Сервис может бросить исключение, если логин занят. Оно поймается в handle()
        var result = participantService.register(dto);
        
        String responseJson = JsonUtils.toJson(new ServerResponseDTO(200, result));
        sendResponse(exchange, 200, responseJson);
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        AuthRequestDTO dto = JsonUtils.fromJson(body, AuthRequestDTO.class);

        // Сервис может бросить исключение, если пароль неверный
        AuthResponseDTO tokenDto = participantService.login(dto);

        String responseJson = JsonUtils.toJson(new ServerResponseDTO(200, tokenDto));
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