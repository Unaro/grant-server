package com.grantserver.common.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Реестр сервисов (Service Locator).
 * Реализует паттерн Singleton.
 * Используется для хранения и получения экземпляров DAO и Service.
 */
public class ServiceRegistry {
    
    private static final ServiceRegistry INSTANCE = new ServiceRegistry();
    private final Map<Class<?>, Object> services = new HashMap<>();

    // Приватный конструктор для Singleton
    private ServiceRegistry() {
    }

    // Глобальная точка доступа
    public static ServiceRegistry getInstance() {
        return INSTANCE;
    }

    // Регистрация компонента (например, ParticipantDAO)
    public <T> void register(Class<T> serviceClass, T implementation) {
        services.put(serviceClass, implementation);
    }

    // Получение компонента
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> serviceClass) {
        return (T) services.get(serviceClass);
    }
}