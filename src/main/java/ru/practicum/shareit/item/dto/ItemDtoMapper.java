package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
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

    public static ItemDtoWithBookings itemToDtoWithBookings(Item item, Booking lastBooking, Booking nextBooking) {
        return new ItemDtoWithBookings(item.getId(), item.getName(), item.getDescription()
                , item.getAvailable(), item.getRequest() == null ? null : item.getRequest().getId()
                , lastBooking == null ? null : BookingDtoMapper.bookingToDtoForItem(lastBooking)
                , nextBooking == null ? null : BookingDtoMapper.bookingToDtoForItem(nextBooking));
    }
}
