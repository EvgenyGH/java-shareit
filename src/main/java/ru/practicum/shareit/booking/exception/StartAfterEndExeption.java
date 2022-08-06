package ru.practicum.shareit.booking.exception;

import java.util.Map;

//Время начало аренда после даты окончания
public class StartAfterEndExeption extends BookingException{
    public StartAfterEndExeption(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
