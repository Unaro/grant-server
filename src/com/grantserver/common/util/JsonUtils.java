package com.grantserver.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    /**
     * Сериализация объекта в JSON строку.
     * Поддерживает: String, Number, Boolean, List (простой), вложенные объекты.
     */
    public static String toJson(Object object) {
        if (object == null) return "null";
        
        Class<?> clazz = object.getClass();
        
        // Обработка примитивов и строк
        if (clazz.equals(String.class)) return "\"" + object + "\"";
        if (Number.class.isAssignableFrom(clazz) || clazz.equals(Boolean.class)) return object.toString();
        
        // Обработка списков
        if (object instanceof List<?>) {
            List<?> list = (List<?>) object;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                sb.append(toJson(list.get(i)));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        }

        // Обработка объектов (DTO)
        StringBuilder json = new StringBuilder("{");
        Field[] fields = clazz.getDeclaredFields();

        try {
            boolean first = true;
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(object);
                
                // Пропускаем null поля, чтобы не засорять JSON
                if (value != null) {
                    if (!first) json.append(",");
                    json.append("\"").append(field.getName()).append("\":");
                    json.append(toJson(value));
                    first = false;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Десериализация JSON строки в объект.
     * Ограничения: 
     * 1. Очень простой парсер, ищет поля по именам.
     * 2. Не поддерживает сложные вложенные структуры (массивы внутри массивов).
     * 3. Рассчитан на корректный JSON от клиента.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        
        // Убираем лишние пробелы и переносы строк для упрощения парсинга
        String cleanJson = json.replaceAll("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)", ""); // удаляет пробелы вне кавычек
        cleanJson = cleanJson.substring(1, cleanJson.length() - 1); // Убираем внешние {}

        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();
            
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = "\"" + field.getName() + "\":";
                
                int index = cleanJson.indexOf(fieldName);
                if (index != -1) {
                    // Начало значения
                    int valueStart = index + fieldName.length();
                    String valueStr = extractValue(cleanJson, valueStart);
                    
                    Object value = parseValue(valueStr, field.getType());
                    field.set(instance, value);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга JSON для класса " + clazz.getSimpleName(), e);
        }
    }

    // Вспомогательный метод для извлечения подстроки значения
    private static String extractValue(String json, int start) {
        char firstChar = json.charAt(start);
        
        // Если это строка
        if (firstChar == '"') {
            int end = json.indexOf("\"", start + 1);
            return json.substring(start, end + 1); // возвращаем с кавычками
        }
        
        // Если это массив
        if (firstChar == '[') {
            int bracketCount = 0;
            for (int i = start; i < json.length(); i++) {
                if (json.charAt(i) == '[') bracketCount++;
                if (json.charAt(i) == ']') bracketCount--;
                if (bracketCount == 0) return json.substring(start, i + 1);
            }
        }

        // Если это объект (nested object)
        if (firstChar == '{') {
            int braceCount = 0;
            for (int i = start; i < json.length(); i++) {
                if (json.charAt(i) == '{') braceCount++;
                if (json.charAt(i) == '}') braceCount--;
                if (braceCount == 0) return json.substring(start, i + 1);
            }
        }
        
        // Если это примитив (число, boolean)
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
            end++;
        }
        return json.substring(start, end);
    }

    // Преобразование строки в нужный тип
    private static Object parseValue(String value, Class<?> type) {
        if (value.startsWith("\"")) {
            return value.substring(1, value.length() - 1); // Убираем кавычки
        }
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }
        if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        // Рекурсивно для вложенных объектов (кроме списков пока)
        if (!type.isPrimitive() && !type.equals(String.class) && !List.class.isAssignableFrom(type)) {
            return fromJson(value, type);
        }
        // TODO: Добавить парсинг List<String>, если понадобится для ExpertRegisterDTO
        if (List.class.isAssignableFrom(type)) {
             // Простая реализация для List<String>
             String content = value.substring(1, value.length() - 1); // убрать []
             String[] parts = content.split(",");
             List<String> list = new ArrayList<>();
             for (String part : parts) {
                 if (part.contains("\"")) {
                     list.add(part.replace("\"", ""));
                 }
             }
             return list;
        }

        return null;
    }
}