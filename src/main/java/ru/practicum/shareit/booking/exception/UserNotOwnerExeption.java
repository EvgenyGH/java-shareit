package ru.practicum.shareit.booking.exception;

import java.util.Map;

//Пользователь не владелец вещи
public class UserNotOwnerExeption extends BookingException{
    public UserNotOwnerExeption(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
