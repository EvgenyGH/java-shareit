package ru.practicum.shareitserver.booking.exception;

import java.util.Map;

//Базовый класс исключений связанных с бронированием
public class BookingException extends RuntimeException {
    private final Map<String, String> properties;

    public BookingException(String message, Map<String, String> properties) {
        super(message);
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}