package ru.practicum.shareit.user.exception;

import java.util.Map;

//Пользователь не найден
public class UserNotFoundException extends UserException{
    public UserNotFoundException(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
