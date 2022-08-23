package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.request.exeption.ItemRequestNotFoundException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice({"ru.practicum.shareit.request"})
@Slf4j
public class ItemRequestExceptionHandler {
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ValidationException.class,
            MethodArgumentTypeMismatchException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationExceptionHandler(Exception exception) {
        log.warn("Выброшено исключение -> {}", exception.getMessage());
        return Map.of("Description", "Ошибка валидации входящих данных.");
    }

    @ExceptionHandler({ItemRequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> itemRequestNotFoundExceptionHandler(ItemRequestNotFoundException exception) {
        log.warn("Запрос вещи id={} не найден.", exception.getProperties().get("Id"));
        return exception.getProperties();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> otherExceptionHandler(Exception exception) {
        log.warn("Неизвестная ошибка -> {}", exception.getMessage());
        return Map.of("Description", "Неизвестная ошибка.");
    }
}
