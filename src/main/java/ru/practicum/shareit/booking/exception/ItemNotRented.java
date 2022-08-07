package ru.practicum.shareit.booking.exception;

import java.util.Map;

//Пользователь ранее не брал в аренду вещь
public class ItemNotRented extends BookingException {
    public ItemNotRented(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
