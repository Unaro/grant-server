package com.grantserver;


import com.grantserver.common.util.JsonUtils;
import java.util.List;

// Временный класс для теста парсера
class DemoUser {
    String name;
    int age;
    List<String> skills;
}

public class TestJson {
    public static void main(String[] args) {
        // Тест 1: Объект -> JSON
        DemoUser user = new DemoUser();
        user.name = "Ivan";
        user.age = 30;
        user.skills = List.of("Java", "NoLibraries");

        String json = JsonUtils.toJson(user);
        System.out.println("JSON Output: " + json);

        // Тест 2: JSON -> Объект
        String inputJson = "{\"name\":\"Petr\",\"age\":25,\"skills\":[\"Coding\"]}";
        DemoUser parsedUser = JsonUtils.fromJson(inputJson, DemoUser.class);
        
        System.out.println("Parsed Name: " + parsedUser.name);
        System.out.println("Parsed Age: " + parsedUser.age);
        System.out.println("Parsed Skills: " + parsedUser.skills);
    }
}