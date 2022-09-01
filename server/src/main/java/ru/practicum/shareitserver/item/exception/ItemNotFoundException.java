package ru.practicum.shareitserver.item.exception;

import java.util.Map;

//Вещь не найдена
public class ItemNotFoundException extends ItemException {
    public ItemNotFoundException(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
