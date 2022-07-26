package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.ItemRequest;

public class ItemRequestDtoMapper {
    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }
}
