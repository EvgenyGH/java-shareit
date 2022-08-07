package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.item.exception.ItemException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice({"ru.practicum.shareit.booking"})
@Slf4j
public class BookingExceptionHandler {
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ValidationException.class
            , MethodArgumentTypeMismatchException.class, ConstraintViolationException.class
            , HttpMessageNotReadableException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> validationExceptionHandler(Exception exception) {
        log.warn("Выброшено исключение -> {}", exception.getMessage());
        return Map.of("Description", "Ошибка валидации входящих данных.");
    }

    @ExceptionHandler({StartAfterEndExeption.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> startAfterEndExceptionHandler(StartAfterEndExeption exception) {
        log.warn("Начало аренды после ее окончания (с {} по {})."
                , exception.getProperties().get("Start")
                , exception.getProperties().get("End"));
        return exception.getProperties();
    }

    @ExceptionHandler({ItemNotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> itemNotAvailableExceptionHandler(ItemNotAvailableException exception) {
        log.warn("Вещь id={} недоступна.", exception.getProperties().get("id"));
        return exception.getProperties();
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> unsupportedStatusExceptionHandler(IllegalArgumentException exception) {
        log.warn("Unsupported Status");
        Map<String, String> response = new HashMap<>();
        response.put("error", "Unknown state: UNSUPPORTED_STATUS");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> itemNotFoundExceptionHandler(ItemException exception) {
        log.warn("Вещь id={} не найдена.", exception.getProperties().get("Id"));
        return exception.getProperties();
    }

    @ExceptionHandler({BookingNotExistsException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> bookingNotExistsHandler(BookingNotExistsException exception) {
        log.warn("Заказ id={} не найден.", exception.getProperties().get("Id"));
        return exception.getProperties();
    }

    @ExceptionHandler({UserNotOwnerExeption.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> userNotOwnerExeptionExceptionHandler(UserNotOwnerExeption exception) {
        log.warn("Пользователь id={} не владелец вещи", exception.getProperties().get("Id"));
        return exception.getProperties();
    }

    @ExceptionHandler({BookingException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> bookingExceptionExceptionHandler(BookingException exception) {
        log.warn("Выброшено BookingException {}.", exception.getProperties());
        return exception.getProperties();
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> otherExceptionHandler(Exception exception) {
        log.warn("Неизвестная ошибка -> {}", exception.getMessage());
        return Map.of("Description", "Неизвестная ошибка.");
    }
}
