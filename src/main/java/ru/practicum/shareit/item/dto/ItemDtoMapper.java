package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;

public class ItemDtoMapper {
    public static ItemDto ItemToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription()
                , item.isAvailable(), item.getRequest_id());
    }

    public static Item dtoToItem(ItemDto itemDto, long ownerId) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription()
                , itemDto.getAvailable(), ownerId, itemDto.getRequest_id());
    }
}
