package ru.practicum.shareitserver.request.exeption;

import java.util.Map;

public class ItemRequestNotFoundException extends ItemRequestException {
    public ItemRequestNotFoundException(String message, Map<String, String> properties) {
        super(message, properties);
    }
}
