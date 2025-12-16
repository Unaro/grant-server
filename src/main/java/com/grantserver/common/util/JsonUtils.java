package com.grantserver.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    /**
     * Сериализация объекта в JSON строку.
     */
    public static String toJson(Object object) {
        if (object == null) return "null";
        
        Class<?> clazz = object.getClass();
        
        if (clazz.equals(String.class)) return "\"" + object + "\"";
        if (Number.class.isAssignableFrom(clazz) || clazz.equals(Boolean.class)) return object.toString();
        
        if (object instanceof List<?> list) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                sb.append(toJson(list.get(i)));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        }

        StringBuilder json = new StringBuilder("{");
        Field[] fields = clazz.getDeclaredFields();

        try {
            boolean first = true;
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(object);
                
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
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        
        String cleanJson = json.replaceAll("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)", ""); 
        cleanJson = cleanJson.substring(1, cleanJson.length() - 1); 

        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();
            
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = "\"" + field.getName() + "\":";
                
                int index = cleanJson.indexOf(fieldName);
                if (index != -1) {
                    int valueStart = index + fieldName.length();
                    String valueStr = extractValue(cleanJson, valueStart);
                    
                    Object value = parseValue(valueStr, field.getType());
                    field.set(instance, value);
                }
            }
            return instance;
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Ошибка парсинга JSON для класса " + clazz.getSimpleName(), e);
        }
    }

    private static String extractValue(String json, int start) {
        char firstChar = json.charAt(start);
        
        if (firstChar == '"') {
            int end = json.indexOf("\"", start + 1);
            return json.substring(start, end + 1);
        }
        
        if (firstChar == '[') {
            int bracketCount = 0;
            for (int i = start; i < json.length(); i++) {
                if (json.charAt(i) == '[') bracketCount++;
                if (json.charAt(i) == ']') bracketCount--;
                if (bracketCount == 0) return json.substring(start, i + 1);
            }
        }

        if (firstChar == '{') {
            int braceCount = 0;
            for (int i = start; i < json.length(); i++) {
                if (json.charAt(i) == '{') braceCount++;
                if (json.charAt(i) == '}') braceCount--;
                if (braceCount == 0) return json.substring(start, i + 1);
            }
        }
        
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
            end++;
        }
        return json.substring(start, end);
    }

    private static Object parseValue(String value, Class<?> type) {
        if (value.startsWith("\"")) {
            return value.substring(1, value.length() - 1); 
        }
        // Целые числа
        if (type == int.class || type == Integer.class) {
            return Integer.valueOf(value);
        }
        // Long
        if (type == long.class || type == Long.class) {
            return Long.valueOf(value);
        }
        // === ДОБАВЛЕНО: Дробные числа (Double) ===
        if (type == double.class || type == Double.class) {
            return Double.valueOf(value);
        }
        // Boolean
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.valueOf(value);
        }
        
        // Рекурсия для объектов
        if (!type.isPrimitive() && !type.equals(String.class) && !List.class.isAssignableFrom(type) && !Number.class.isAssignableFrom(type)) {
            return fromJson(value, type);
        }
        
        // Списки строк
        if (List.class.isAssignableFrom(type)) {
             String content = value.substring(1, value.length() - 1);
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