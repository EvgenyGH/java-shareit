package ru.practicum.shareit.user.exception;

import java.util.Map;

//Почта уже принадлежит другому пользователю
public class EmailExistsException extends UserException {
    public EmailExistsException(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
