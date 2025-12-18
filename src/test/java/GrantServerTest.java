import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.grantserver.Main;
import com.sun.net.httpserver.HttpServer;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GrantServerTest {

    private static HttpServer server;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080/api";

    // Состояние для тестов
    private static String participantToken;
    private static String expertToken;
    
    @BeforeAll
    public static void setUp() throws Exception {
        server = Main.startServer();
        // Даем серверу время на старт
        Thread.sleep(500);
    }

    @AfterAll
    public static void tearDown() {
        if (server != null) {
            server.stop(0);
            System.out.println("Сервер остановлен.");
        }
    }

    // --- БЛОК 1: БАЗОВЫЕ ПРОВЕРКИ ---

    @Test
    @Order(1)
    @DisplayName("1. Health Check - Сервер жив")
    public void testHealthCheck() throws Exception {
        HttpResponse<String> response = sendGet("/health", null);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("OK"));
    }

    // --- БЛОК 2: АВТОРИЗАЦИЯ И ОШИБКИ ---

    @Test
    @Order(2)
    @DisplayName("2. Успешная регистрация участника")
    public void testRegisterParticipant() throws Exception {
        String json = "{\"firmName\":\"JUnitCorp\",\"manager\":{\"firstName\":\"J\",\"lastName\":\"Unit\"},\"login\":\"junit_user\",\"password\":\"123\"}";
        HttpResponse<String> response = sendPost("/participants/register", json, null);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"id\":1"));
    }

    @Test
    @Order(3)
    @DisplayName("3. ОШИБКА: Регистрация дубликата логина")
    public void testRegisterDuplicate() throws Exception {
        // Пытаемся зарегистрировать того же юзера
        String json = "{\"firmName\":\"Clone\",\"manager\":{\"firstName\":\"C\",\"lastName\":\"Lone\"},\"login\":\"junit_user\",\"password\":\"123\"}";
        HttpResponse<String> response = sendPost("/participants/register", json, null);
        
        // Ожидаем ошибку (400 Bad Request или 409 Conflict в зависимости от вашей реализации)
        assertTrue(response.statusCode() >= 400, "Should return error code for duplicate login");
    }

    @Test
    @Order(4)
    @DisplayName("4. Успешный вход участника")
    public void testLoginParticipant() throws Exception {
        String json = "{\"login\":\"junit_user\",\"password\":\"123\"}";
        HttpResponse<String> response = sendPost("/participants/login", json, null);
        assertEquals(200, response.statusCode());
        participantToken = extractToken(response.body());
        assertNotNull(participantToken);
    }

    @Test
    @Order(5)
    @DisplayName("5. ОШИБКА: Вход с неверным паролем")
    public void testLoginInvalidPassword() throws Exception {
        String json = "{\"login\":\"junit_user\",\"password\":\"WRONG_PASS\"}";
        HttpResponse<String> response = sendPost("/participants/login", json, null);
        // Ожидаем 400 или 401
        assertTrue(response.statusCode() >= 400);
    }

    // --- БЛОК 3: ЗАЯВКИ ---

    @Test
    @Order(6)
    @DisplayName("6. ОШИБКА: Создание заявки без токена")
    public void testCreateApplicationUnauthorized() throws Exception {
        String json = "{\"title\":\"Hacker App\",\"description\":\"...\",\"fields\":[\"IT\"],\"requestedSum\":1000}";
        // Отправляем БЕЗ токена
        HttpResponse<String> response = sendPost("/applications", json, null);
        assertEquals(401, response.statusCode());
    }

    @Test
    @Order(7)
    @DisplayName("7. Создание заявки (Happy Path)")
    public void testCreateApplication() throws Exception {
        String json = "{\"title\":\"JUnit Grant\",\"description\":\"Testing\",\"fields\":[\"IT\"],\"requestedSum\":1000}";
        HttpResponse<String> response = sendPost("/applications", json, participantToken);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("JUnit Grant"));
    }

    @Test
    @Order(8)
    @DisplayName("8. Получение списка всех заявок")
    public void testGetAllApplications() throws Exception {
        HttpResponse<String> response = sendGet("/applications", participantToken);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("JUnit Grant"), "List should contain created app");
    }

    // --- БЛОК 4: ЭКСПЕРТЫ И ОЦЕНКИ ---

    @Test
    @Order(9)
    @DisplayName("9. Регистрация и вход Эксперта")
    public void testExpertFlow() throws Exception {
        String regJson = "{\"firstName\":\"Exp\",\"lastName\":\"Test\",\"fields\":[\"IT\"],\"login\":\"exp_junit\",\"password\":\"123\"}";
        sendPost("/experts/register", regJson, null);

        String authJson = "{\"login\":\"exp_junit\",\"password\":\"123\"}";
        HttpResponse<String> response = sendPost("/experts/login", authJson, null);
        assertEquals(200, response.statusCode());
        expertToken = extractToken(response.body());
    }

    @Test
    @Order(10)
    @DisplayName("10. Оценка заявки")
    public void testRateApplication() throws Exception {
        // Оцениваем заявку №1 на 3.0 балла
        String json = "{\"applicationId\":1, \"expertId\":1, \"score\":3.0}";
        HttpResponse<String> response = sendPost("/evaluations", json, expertToken);
        assertEquals(200, response.statusCode());
    }

    @Test
    @Order(11)
    @DisplayName("11. Обновление оценки (PUT)")
    public void testUpdateEvaluation() throws Exception {
        // Изменяем оценку с 3.0 на 5.0
        // Предполагаем, что ID оценки = 1 (так как она первая)
        String json = "{\"applicationId\":1, \"expertId\":1, \"score\":5.0}";
        HttpResponse<String> response = sendPut("/evaluations/1", json, expertToken);
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"score\":5.0"));
    }

    @Test
    @Order(12)
    @DisplayName("12. Удаление оценки (DELETE)")
    public void testDeleteEvaluation() throws Exception {
        // Удаляем оценку №1
        HttpResponse<String> response = sendDelete("/evaluations/1", expertToken);
        assertEquals(200, response.statusCode());
        
        // Проверяем, что ее больше нет, пытаясь обновить снова (должна быть ошибка) или просто верим коду 200
        // Для надежности можно создать новую оценку для следующих тестов
    }

    // --- БЛОК 5: СЛОЖНЫЙ СЦЕНАРИЙ (ИТОГИ) ---

    @Test
    @Order(13)
    @DisplayName("13. Подготовка данных для конкурса")
    public void testPrepareComplexScenario() throws Exception {
        // Создаем новую оценку для Заявки 1 (так как старую удалили)
        // Оценка 5.0
        String jsonEval = "{\"applicationId\":1, \"expertId\":1, \"score\":5.0}";
        sendPost("/evaluations", jsonEval, expertToken);

        // Регистрируем второго участника
        String user2 = "{\"firmName\":\"LoserCorp\",\"manager\":{\"firstName\":\"L\",\"lastName\":\"Oser\"},\"login\":\"loser_user\",\"password\":\"123\"}";
        sendPost("/participants/register", user2, null);
        // Логинимся
        String login2 = "{\"login\":\"loser_user\",\"password\":\"123\"}";
        HttpResponse<String> respLogin = sendPost("/participants/login", login2, null);
        String token2 = extractToken(respLogin.body());

        // Создаем Заявку 2 (Дорогая и плохая)
        // Сумма 5000 (больше фонда), Оценка будет низкой
        String app2 = "{\"title\":\"Expensive App\",\"description\":\"...\",\"fields\":[\"IT\"],\"requestedSum\":5000}";
        sendPost("/applications", app2, token2);
        
        // Эксперт оценивает Заявку 2 на 2.0 балла
        // ID заявки будет 2 (автоинкремент)
        String eval2 = "{\"applicationId\":2, \"expertId\":1, \"score\":2.0}";
        sendPost("/evaluations", eval2, expertToken);
    }

    @Test
    @Order(14)
    @DisplayName("14. Расчет итогов (Конкуренция)")
    public void testComputeGrantsComplex() throws Exception {
        // Фонд 2000. 
        // Заявка 1: Сумма 1000, Балл 5.0 -> Должна выиграть.
        // Заявка 2: Сумма 5000, Балл 2.0 -> Проиграет (мало баллов + мало денег).
        
        String json = "{\"fund\":2000, \"threshold\":3.0}";
        HttpResponse<String> response = sendPost("/grants/compute", json, participantToken);
        
        assertEquals(200, response.statusCode());
        String body = response.body();
        
        // Проверки
        assertTrue(body.contains("JUnit Grant"), "First app should win");
        assertFalse(body.contains("Expensive App"), "Second app should lose");
        assertTrue(body.contains("\"remainingFund\":1000"), "Should have 1000 left (2000 - 1000)");
    }

    // --- HELPERS (Вспомогательные методы) ---

    private HttpResponse<String> sendGet(String path, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET();
        if (token != null) builder.header("Authorization", "Bearer " + token);
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPost(String path, String json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        
        if (token != null) builder.header("Authorization", "Bearer " + token);
        
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    // НОВЫЙ МЕТОД: PUT
    private HttpResponse<String> sendPut(String path, String json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json)); // PUT метод
        
        if (token != null) builder.header("Authorization", "Bearer " + token);
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    // НОВЫЙ МЕТОД: DELETE
    private HttpResponse<String> sendDelete(String path, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE(); // DELETE метод
        
        if (token != null) builder.header("Authorization", "Bearer " + token);
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String extractToken(String json) {
        if (json.contains("responseData")) {
            int start = json.indexOf("\"responseData\":");
            // Простой парсинг, ищем токен внутри data
            // Предполагаем структуру: { ... "token": "UUID" ... }
            int tokenKey = json.indexOf("\"token\":", start);
            if (tokenKey != -1) {
                int valStart = json.indexOf("\"", tokenKey + 8) + 1;
                int valEnd = json.indexOf("\"", valStart);
                return json.substring(valStart, valEnd);
            }
        }
        return null;
    }
}