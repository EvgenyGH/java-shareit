package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

public class ItemRequestDtoMapper {
    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription()
                , itemRequest.getCreatedDateTime());
    }
}
