package com.grantserver.api.handlers;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.common.util.JsonUtils;
import com.grantserver.dto.request.GrantFundRequestDTO;
import com.grantserver.dto.response.GrantResultDTO;
import com.grantserver.dto.response.ServerResponseDTO;
import com.grantserver.service.GrantFundService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class GrantFundHandler implements HttpHandler {

    private final GrantFundService grantFundService;

    public GrantFundHandler() {
        this.grantFundService = ServiceRegistry.getInstance().get(GrantFundService.class);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        
        try {
            if ("POST".equals(method)) {
                handleCompute(exchange);
            } else {
                sendResponse(exchange, 404, "Endpoint not found or method not supported");
            }
        } catch (IOException e) {
            sendResponse(exchange, 400, "Error: " + e.getMessage());
        }
    }

    private void handleCompute(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());
        if (body == null || body.trim().isEmpty()) {
            sendResponse(exchange, 400, "Request body is empty");
            return;
        }

        GrantFundRequestDTO dto = JsonUtils.fromJson(body, GrantFundRequestDTO.class);
        if (dto == null) {
            sendResponse(exchange, 400, "Invalid JSON");
            return;
        }

        GrantResultDTO result = grantFundService.calculate(dto);
        
        sendResponse(exchange, 200, result);
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