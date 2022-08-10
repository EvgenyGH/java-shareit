package ru.practicum.shareit.booking.exception;

import java.util.Map;

//Заказа не существует
public class BookingNotExistsException extends BookingException {
    public BookingNotExistsException(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
