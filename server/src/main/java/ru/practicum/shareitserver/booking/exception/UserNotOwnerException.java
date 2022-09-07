package ru.practicum.shareitserver.booking.exception;

import java.util.Map;

//Пользователь не владелец вещи
public class UserNotOwnerException extends BookingException {
    public UserNotOwnerException(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
