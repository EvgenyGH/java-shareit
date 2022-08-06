package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.exception.ItemNotAvailableException;
import ru.practicum.shareit.booking.exception.StartAfterEndExeption;
import ru.practicum.shareit.item.exception.ItemException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice({"ru.practicum.shareit.booking"})
@Slf4j
public class BookingExceptionHandler {
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ValidationException.class
            , MethodArgumentTypeMismatchException.class, ConstraintViolationException.class
            , HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationExceptionHandler(Exception exception) {
        log.warn("Выброшено исключение -> {}", exception.getMessage());
        return Map.of("Description", "Ошибка валидации входящих данных.");
    }

    @ExceptionHandler({StartAfterEndExeption.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> userNotFoundExceptionHandler(StartAfterEndExeption exception) {
        log.warn("Начало аренды после ее окончания (с {} по {})."
                , exception.getProperties().get("Start")
                , exception.getProperties().get("End"));
        return exception.getProperties();
    }

    @ExceptionHandler({ItemNotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> itemNotFoundExceptionHandler(ItemNotAvailableException exception) {
        log.warn("Вещь id={} недоступна.", exception.getProperties().get("id"));
        return exception.getProperties();
    }

    @ExceptionHandler({ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> itemNotFoundExceptionHandler(ItemException exception) {
        log.warn("Вещь id={} не найдена.", exception.getProperties().get("Id"));
        return exception.getProperties();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> otherExceptionHandler(Exception exception) {
        log.warn("Неизвестная ошибка -> {}", exception.getMessage());
        return Map.of("Description", "Неизвестная ошибка.");
    }
}
