package com.grantserver;

import com.grantserver.common.util.JsonUtils;
import com.grantserver.dto.request.AuthRequestDTO;
import com.grantserver.dto.response.AuthResponseDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class TestRunner {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newHttpClient();
    
    // Цвета для консоли
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";

    public static void main(String[] args) {
        System.out.println("=== ЗАПУСК ТЕСТОВ GRANT SERVER ===");

        // 1. Запускаем сервер в отдельном потоке
        CompletableFuture.runAsync(() -> {
            try {
                Main.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Ждем секунду, пока сервер поднимется
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        try {
            // 2. Запуск тестов
            testHealthCheck();
            testJsonUtils(); // Unit test
            testFullIntegrationScenario(); // End-to-End test

            System.out.println("\n" + ANSI_GREEN + "ВСЕ ТЕСТЫ ПРОШЛИ УСПЕШНО!" + ANSI_RESET);
            
        } catch (Exception e) {
            System.out.println("\n" + ANSI_RED + "ТЕСТЫ ПРОВАЛЕНЫ: " + e.getMessage() + ANSI_RESET);
            e.printStackTrace();
        } finally {
            // Завершаем программу (и сервер вместе с ней)
            System.exit(0);
        }
    }

    // --- ТЕСТЫ ---

    private static void testHealthCheck() throws Exception {
        System.out.print("Test 1: Health Check... ");
        HttpResponse<String> response = sendGet("/health");
        assertEquals(200, response.statusCode());
        assertContains(response.body(), "GrantServer v1.0");
        printPass();
    }

    private static void testJsonUtils() {
        System.out.print("Test 2: JsonUtils Unit Test... ");
        // Проверка сериализации
        String json = JsonUtils.toJson(new AuthRequestDTO()); // Просто пустой объект
        if (!json.contains("{")) throw new RuntimeException("JSON serialization failed");
        
        // Проверка десериализации Double (то, что мы чинили)
        String doubleJson = "{\"fund\": 1000, \"threshold\": 3.5}";
        try {
            com.grantserver.dto.request.GrantFundRequestDTO dto = JsonUtils.fromJson(doubleJson, com.grantserver.dto.request.GrantFundRequestDTO.class);
            if (dto.threshold != 3.5) throw new RuntimeException("Double parsing failed: expected 3.5, got " + dto.threshold);
        } catch (Exception e) {
             throw new RuntimeException("JsonUtils failed: " + e.getMessage());
        }
        printPass();
    }

    private static void testFullIntegrationScenario() throws Exception {
        System.out.println("\n--- Запуск полного сценария ---");

        // 1. Регистрация участника
        System.out.print("Step 1: Register Participant... ");
        String pReg = "{\"firmName\":\"TestFirm\",\"manager\":{\"firstName\":\"T\",\"lastName\":\"F\"},\"login\":\"user1\",\"password\":\"123\"}";
        HttpResponse<String> regResp = sendPost("/participants/register", pReg, null);
        assertEquals(200, regResp.statusCode());
        printPass();

        // 2. Логин участника
        System.out.print("Step 2: Login Participant... ");
        String pAuth = "{\"login\":\"user1\",\"password\":\"123\"}";
        HttpResponse<String> loginResp = sendPost("/participants/login", pAuth, null);
        assertEquals(200, loginResp.statusCode());
        
        // Пример ответа: {"token":"uuid..."}
        String body = loginResp.body();
        // В JsonUtils мы писали простой парсер
        AuthResponseDTO authDTO = JsonUtils.fromJson(extractResponseData(body), AuthResponseDTO.class);
        String token = authDTO.token;
        if (token == null || token.length() < 10) throw new RuntimeException("Token invalid");
        printPass();

        // 3. Создание заявки
        System.out.print("Step 3: Create Application... ");
        String appJson = "{\"title\":\"My Grant\",\"description\":\"Desc\",\"fields\":[\"IT\"],\"requestedSum\":100}";
        // Передаем X-User-Id: 1 (так как это первый юзер)
        HttpResponse<String> appResp = sendPost("/applications", appJson, "1"); 
        assertEquals(200, appResp.statusCode());
        printPass();

        // 4. Регистрация эксперта
        System.out.print("Step 4: Register Expert... ");
        String eReg = "{\"firstName\":\"Exp\",\"lastName\":\"Man\",\"fields\":[\"IT\"],\"login\":\"expert1\",\"password\":\"123\"}";
        sendPost("/experts/register", eReg, null);
        printPass();

        // 5. Оценка заявки (Заявка ID=1, Эксперт ID=1)
        System.out.print("Step 5: Rate Application... ");
        String evalJson = "{\"applicationId\":1, \"expertId\":1, \"score\":5}";
        HttpResponse<String> evalResp = sendPost("/evaluations", evalJson, null);
        assertEquals(200, evalResp.statusCode());
        printPass();

        // 6. Подведение итогов
        System.out.print("Step 6: Compute Grants... ");
        String fundJson = "{\"fund\":1000, \"threshold\":3.0}";
        HttpResponse<String> fundResp = sendPost("/grants/compute", fundJson, null);
        assertEquals(200, fundResp.statusCode());
        
        // Проверяем, что в winners есть наша заявка
        if (!fundResp.body().contains("My Grant")) {
            throw new RuntimeException("Application 'My Grant' should be a winner but wasn't.");
        }
        printPass();
    }

    // --- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ (Mini-Framework) ---

    private static HttpResponse<String> sendGet(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> sendPost(String endpoint, String json, String userIdHeader) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        
        if (userIdHeader != null) {
            builder.header("X-User-Id", userIdHeader);
        }

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new RuntimeException("Expected status " + expected + " but got " + actual);
        }
    }

    private static void assertContains(String content, String subString) {
        if (!content.contains(subString)) {
            throw new RuntimeException("Response did not contain '" + subString + "'. Got: " + content);
        }
    }

    private static void printPass() {
        System.out.println(ANSI_GREEN + "[PASS]" + ANSI_RESET);
    }


    private static String extractResponseData(String serverResponseJson) {
        // Ответ приходит вида: {"responseCode":200,"responseData":{...}}
        int start = serverResponseJson.indexOf("\"responseData\":");
        if (start == -1) return "{}";
        String sub = serverResponseJson.substring(start + 15); 
        return sub.substring(0, sub.lastIndexOf("}"));
    }
}