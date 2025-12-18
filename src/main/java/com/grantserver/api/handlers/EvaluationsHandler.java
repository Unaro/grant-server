package com.grantserver.api.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.grantserver.common.auth.SessionManager;
import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.common.util.JsonUtils;
import com.grantserver.dto.request.EvaluationCreateDTO;
import com.grantserver.dto.response.EvaluationDTO;
import com.grantserver.dto.response.ServerResponseDTO;
import com.grantserver.service.EvaluationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EvaluationsHandler implements HttpHandler {

    private final EvaluationService evaluationService;

    public EvaluationsHandler() {
        this.evaluationService = ServiceRegistry.getInstance().get(EvaluationService.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            // POST /api/evaluations - Создать оценку
            if ("POST".equals(method) && path.endsWith("/api/evaluations")) {
                handleCreate(exchange);
            } 
            // PUT /api/evaluations/{id} - Обновить оценку
            else if ("PUT".equals(method) && path.contains("/api/evaluations/")) {
                Long id = extractIdFromPath(path);
                handleUpdate(exchange, id);
            } 
            // DELETE /api/evaluations/{id} - Удалить оценку
            else if ("DELETE".equals(method) && path.contains("/api/evaluations/")) {
                Long id = extractIdFromPath(path);
                handleDelete(exchange, id);
            } 
            else {
                sendResponse(exchange, 404, "Endpoint not found");
            }
        } catch (IOException e) {
            sendResponse(exchange, 400, "Error: " + e.getMessage());
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        
        // 1. Проверка прав эксперта
        Long expertId = getExpertIdFromToken(exchange);
        if (expertId == null) {
            sendResponse(exchange, 401, "Unauthorized: Invalid or missing expert token");
            return;
        }
        
        String body = readRequestBody(exchange.getRequestBody());

        if (body == null || body.trim().isEmpty()) {
            sendResponse(exchange, 400, "Request body is empty");
            return;
        }

        EvaluationCreateDTO dto = JsonUtils.fromJson(body, EvaluationCreateDTO.class);
        if (dto == null) {
            sendResponse(exchange, 400, "Invalid JSON");
            return;
        }

        dto.expertId = expertId;

        EvaluationDTO created = evaluationService.createEvaluation(dto, expertId);
        sendResponse(exchange, 200, created);
    }

    private void handleUpdate(HttpExchange exchange, Long id) throws IOException {
        
        // 1. Проверка прав эксперта
        Long expertId = getExpertIdFromToken(exchange);
        if (expertId == null) {
            sendResponse(exchange, 401, "Unauthorized");
            return;
        }
        
        String body = readRequestBody(exchange.getRequestBody());
        EvaluationCreateDTO dto = JsonUtils.fromJson(body, EvaluationCreateDTO.class);
        
        dto.expertId = expertId;

        EvaluationDTO updated = evaluationService.updateEvaluation(id, dto);
        sendResponse(exchange, 200, updated);
    }

    private void handleDelete(HttpExchange exchange, Long id) throws IOException {
        // 1. Проверка на авторизацию
        if (getExpertIdFromToken(exchange) == null) {
            sendResponse(exchange, 401, "Unauthorized");
            return;
        }
       
        evaluationService.deleteEvaluation(id);
        sendResponse(exchange, 200, "Evaluation deleted successfully");
    }

    // --- Утилиты ---

    private Long getExpertIdFromToken(HttpExchange exchange) {
        List<String> authHeader = exchange.getRequestHeaders().get("Authorization");
        if (authHeader == null || authHeader.isEmpty()) return null;

        String token = authHeader.get(0);
        if (token.startsWith("Bearer ")) token = token.substring(7);

        return SessionManager.getInstance().getUserId(token);
    }


    private Long extractIdFromPath(String path) {
        try {
            // Берем всё, что после последнего слеша
            String idPart = path.substring(path.lastIndexOf('/') + 1);
            return Long.valueOf(idPart);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid ID in path: " + path);
        }
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