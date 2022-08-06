package ru.practicum.shareit.booking.exception;

import java.util.Map;

//Вещь недоступна
public class ItemNotAvailableException extends BookingException{
    public ItemNotAvailableException(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
