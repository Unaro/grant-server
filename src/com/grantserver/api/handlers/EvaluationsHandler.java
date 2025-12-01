package com.grantserver.api.handlers;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.common.util.JsonUtils;
import com.grantserver.dto.request.EvaluationCreateDTO;
import com.grantserver.dto.response.EvaluationDTO;
import com.grantserver.dto.response.ServerResponseDTO;
import com.grantserver.service.EvaluationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Error: " + e.getMessage());
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
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

        EvaluationDTO created = evaluationService.addEvaluation(dto);
        sendResponse(exchange, 200, created);
    }

    private void handleUpdate(HttpExchange exchange, Long id) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());
        EvaluationCreateDTO dto = JsonUtils.fromJson(body, EvaluationCreateDTO.class);
        
        EvaluationDTO updated = evaluationService.updateEvaluation(id, dto);
        sendResponse(exchange, 200, updated);
    }

    private void handleDelete(HttpExchange exchange, Long id) throws IOException {
        evaluationService.deleteEvaluation(id);
        sendResponse(exchange, 200, "Evaluation deleted successfully");
    }

    // --- Утилиты ---

    private Long extractIdFromPath(String path) {
        try {
            // Берем всё, что после последнего слеша
            String idPart = path.substring(path.lastIndexOf('/') + 1);
            return Long.parseLong(idPart);
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