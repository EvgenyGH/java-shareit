package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.exception.EmailExistsException;
import ru.practicum.shareit.user.exception.UserException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.Map;

@RestControllerAdvice({"ru.practicum.shareit.user"})
@Slf4j
public class UserExceptionHandler {
    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> userNotFoundExceptionHandler (UserException exception){
        log.warn("Пользователь id={} не найден.", exception.getProperties().get("Id"));
        return exception.getProperties();
    }

    @ExceptionHandler({EmailExistsException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> emailExistsExceptionHandler (UserException exception){
        log.warn("Пользователь с почтой {} уже существует.", exception.getProperties().get("Value"));
        return exception.getProperties();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> otherExceptionHandler (Exception exception){
        log.warn("Неизвестная ошибка -> {}", exception.getMessage());
        return Map.of("Description", "Неизвестная ошибка.");
    }
}
