package ru.practicum.shareitgateway.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice({"ru.practicum.shareitgateway"})
@Slf4j
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {MethodArgumentNotValidException.class, ValidationException.class,
            MethodArgumentTypeMismatchException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationExceptionHandler(Exception exception) {
        log.warn("Выброшено исключение -> {}", exception.getMessage());
        return Map.of("Description", "Ошибка валидации входящих данных.");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> otherExceptionHandler(Exception exception) {
        log.warn("Неизвестная ошибка -> {}", exception.getMessage());
        return Map.of("Description", "Неизвестная ошибка.");
    }
}
