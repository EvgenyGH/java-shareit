package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemRequestDtoMapper {
    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getCreatedDateTime());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(),
                requestor, itemRequestDto.getCreated());
    }

    public static ItemRequestDtoWithResponse itemRequestToDtoWithResponse(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDtoWithResponse(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getCreatedDateTime(), items);
    }
}
