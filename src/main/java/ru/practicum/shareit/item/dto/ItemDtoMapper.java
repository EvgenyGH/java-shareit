package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

public class ItemDtoMapper {
    public static ItemDto ItemToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription()
                , item.getAvailable(), item.getRequest() == null ? null : item.getRequest().getId());
    }

    public static Item dtoToItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return new Item(itemDto.getId()
                , itemDto.getName()
                , itemDto.getDescription()
                , itemDto.getAvailable() == null || itemDto.getAvailable()
                , owner
                , itemRequest);
    }
}
