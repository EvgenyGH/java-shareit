package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.exception.ItemNotRentedException;
import ru.practicum.shareit.item.exception.ItemException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.UserException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice({"ru.practicum.shareit.item"})
@Slf4j
public class ItemExceptionHandler {
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ValidationException.class
            , MethodArgumentTypeMismatchException.class, ConstraintViolationException.class
            , HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationExceptionHandler(Exception exception) {
        log.warn("Выброшено исключение -> {}", exception.getMessage());
        return Map.of("Description", "Ошибка валидации входящих данных.");
    }

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> userNotFoundExceptionHandler(UserException exception) {
        log.warn("Пользователь id={} не найден.", exception.getProperties().get("Id"));
        return exception.getProperties();
    }

    @ExceptionHandler({ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> itemNotFoundExceptionHandler(ItemException exception) {
        log.warn("Вещь id={} не найдена.", exception.getProperties().get("Id"));
        return exception.getProperties();
    }

    @ExceptionHandler({ItemNotRentedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> itemNotRentedExceptionHandler(ItemNotRentedException exception) {
        log.warn("Пользователь id={} не арендовал вещь id={}"
                , exception.getProperties().get("UserId")
                , exception.getProperties().get("ItemId"));
        return exception.getProperties();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> otherExceptionHandler(Exception exception) {
        log.warn("Неизвестная ошибка -> {}", exception.getMessage());
        return Map.of("Description", "Неизвестная ошибка.");
    }
}
