package ru.practicum.shareitserver.item.exception;

import java.util.Map;

//Базовый класс исключений связанных с вещью
public class ItemException extends RuntimeException {
    private final Map<String, String> properties;

    public ItemException(String message, Map<String, String> properties) {
        super(message);
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}