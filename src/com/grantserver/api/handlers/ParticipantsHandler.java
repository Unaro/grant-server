package com.grantserver.api.handlers;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.common.util.JsonUtils;
import com.grantserver.dto.request.ParticipantRegisterDTO;
import com.grantserver.dto.response.ParticipantDTO;
import com.grantserver.dto.response.ServerResponseDTO;
import com.grantserver.service.ParticipantService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ParticipantsHandler implements HttpHandler {

    private final ParticipantService participantService;

    public ParticipantsHandler() {
        this.participantService = ServiceRegistry.getInstance().get(ParticipantService.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Маршрутизация (Routing)
        try {
            if ("POST".equals(method) && path.endsWith("/register")) {
                handleRegister(exchange);
            } else {
                // Если метод или путь не найдены
                sendResponse(exchange, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            // Глобальная обработка ошибок
            e.printStackTrace(); // Полезно видеть в консоли
            sendResponse(exchange, 400, e.getMessage());
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        // 1. Читаем тело запроса
        String body = readRequestBody(exchange.getRequestBody());
        
        // 2. Парсим JSON
        ParticipantRegisterDTO dto = JsonUtils.fromJson(body, ParticipantRegisterDTO.class);
        
        // 3. Вызываем бизнес-логику
        ParticipantDTO created = participantService.register(dto);
        
        // 4. Отправляем успешный ответ (200 OK)
        sendResponse(exchange, 200, created);
    }

    // --- Утилитные методы (можно потом вынести в BaseHandler) ---

    private void sendResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        // Оборачиваем в ServerResponseDTO согласно спецификации
        ServerResponseDTO responseDTO = new ServerResponseDTO(statusCode, data);
        String jsonResponse = JsonUtils.toJson(responseDTO);
        
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length); // HTTP статус всегда 200, код ошибки внутри JSON
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readRequestBody(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}