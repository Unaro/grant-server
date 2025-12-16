

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.grantserver.Main;
import com.grantserver.common.util.JsonUtils;
import com.grantserver.dto.response.AuthResponseDTO;
import com.sun.net.httpserver.HttpServer;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GrantServerTest {

    private static HttpServer server;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080/api";

    private static String participantToken;
    private static String expertToken;

    @BeforeAll
    public static void setUp() throws Exception {
        server = Main.startServer();
        Thread.sleep(500);
    }

    @AfterAll
    public static void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    // --- ТЕСТЫ ---

    @Test
    @Order(1)
    @DisplayName("1. Проверка доступности сервера (Health Check)")
    public void testHealthCheck() throws Exception {
        HttpResponse<String> response = sendGet("/health");
        assertEquals(200, response.statusCode(), "Status should be 200");
        assertTrue(response.body().contains("OK"), "Body should contain OK");
    }

    @Test
    @Order(2)
    @DisplayName("2. Регистрация участника")
    public void testRegisterParticipant() throws Exception {
        String json = "{\"firmName\":\"JUnitCorp\",\"manager\":{\"firstName\":\"J\",\"lastName\":\"Unit\"},\"login\":\"junit_user\",\"password\":\"123\"}";
        HttpResponse<String> response = sendPost("/participants/register", json);
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"id\":1"), "ID should be 1 for first user");
    }

    @Test
    @Order(3)
    @DisplayName("3. Вход участника (Получение токена)")
    public void testLoginParticipant() throws Exception {
        String json = "{\"login\":\"junit_user\",\"password\":\"123\"}";
        HttpResponse<String> response = sendPost("/participants/login", json);
        
        assertEquals(200, response.statusCode());
        
        // Извлекаем токен
        participantToken = extractToken(response.body());
        assertNotNull(participantToken, "Token should not be null");
        assertTrue(participantToken.length() > 10, "Token looks like UUID");
    }

    @Test
    @Order(4)
    @DisplayName("4. Создание заявки (с токеном)")
    public void testCreateApplication() throws Exception {
        assertNotNull(participantToken, "Skipping test: No token from previous step");

        String json = "{\"title\":\"JUnit Grant\",\"description\":\"Testing\",\"fields\":[\"IT\"],\"requestedSum\":1000}";
        HttpResponse<String> response = sendPost("/applications", json, participantToken);
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("JUnit Grant"));
    }

    @Test
    @Order(5)
    @DisplayName("5. Регистрация и вход Эксперта")
    public void testExpertFlow() throws Exception {
        // 1. Регистрация
        String regJson = "{\"firstName\":\"Exp\",\"lastName\":\"Test\",\"fields\":[\"IT\"],\"login\":\"exp_junit\",\"password\":\"123\"}";
        sendPost("/experts/register", regJson);

        // 2. Вход
        String authJson = "{\"login\":\"exp_junit\",\"password\":\"123\"}";
        HttpResponse<String> response = sendPost("/experts/login", authJson);
        
        assertEquals(200, response.statusCode());
        expertToken = extractToken(response.body());
        assertNotNull(expertToken);
    }

    @Test
    @Order(6)
    @DisplayName("6. Оценка заявки (с токеном эксперта)")
    public void testRateApplication() throws Exception {
        assertNotNull(expertToken, "Skipping: No expert token");

        // Оцениваем заявку ID=1 (созданную в шаге 4)
        String json = "{\"applicationId\":1, \"expertId\":1, \"score\":5}";
        HttpResponse<String> response = sendPost("/evaluations", json, expertToken);
        
        assertEquals(200, response.statusCode());
    }

    @Test
    @Order(7)
    @DisplayName("7. Расчет итогов")
    public void testComputeGrants() throws Exception {
        String json = "{\"fund\":2000, \"threshold\":3.0}";
        HttpResponse<String> response = sendPost("/grants/compute", json, participantToken); // Токен тут любой подойдет или без него, если метод открыт
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("JUnit Grant"), "Application should be in winners list");
    }

    // --- Helpers ---

    private HttpResponse<String> sendGet(String path) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET();
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    

    private HttpResponse<String> sendPost(String path, String json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        
        builder.header("Authorization", "Bearer " + token);
        
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

        private HttpResponse<String> sendPost(String path, String json) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String extractToken(String json) {
        if (json.contains("responseData")) {
            int start = json.indexOf("\"responseData\":");
            String sub = json.substring(start + 15);
            sub = sub.substring(0, sub.lastIndexOf("}"));
            AuthResponseDTO dto = JsonUtils.fromJson(sub, AuthResponseDTO.class);
            return dto.token;
        }
        return null;
    }
}