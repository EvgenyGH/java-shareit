package ru.practicum.shareit.request.exeption;

import java.util.Map;

public class ItemRequestNotFound extends ItemRequestException {
    public ItemRequestNotFound(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
