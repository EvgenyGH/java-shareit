package ru.practicum.shareitserver.request.exeption;

import java.util.Map;

//Базовый класс исключений связанных с запросом вещи
public class ItemRequestException extends RuntimeException {
    private final Map<String, String> properties;

    public ItemRequestException(String message, Map<String, String> properties) {
        super(message);
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}

