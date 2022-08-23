package ru.practicum.shareit.booking.exception;

import java.util.Map;

//Время начало аренда после даты окончания
public class StartAfterEndException extends BookingException {
    public StartAfterEndException(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
