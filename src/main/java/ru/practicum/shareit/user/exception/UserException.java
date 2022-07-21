package ru.practicum.shareit.user.exception;

import java.util.Map;

//Базовый класс исключений связанных с пользователем
public class UserException extends RuntimeException {
    private final Map<String, String> properties;

    public UserException(String message, Map<String, String> properties) {
        super(message);
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
