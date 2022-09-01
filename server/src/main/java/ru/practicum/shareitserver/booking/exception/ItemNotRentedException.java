package ru.practicum.shareitserver.booking.exception;

import java.util.Map;

//Пользователь ранее не брал в аренду вещь
public class ItemNotRentedException extends BookingException {
    public ItemNotRentedException(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
