package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemDtoMapper {
    public static ItemDto itemToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequest() == null ? null : item.getRequest().getId());
    }

    public static Item dtoToItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable() == null || itemDto.getAvailable(), owner, itemRequest);
    }

    public static ItemDtoWithBookings itemToDtoWithBookings(Item item, Booking lastBooking,
                                                            Booking nextBooking, List<Comment> comments) {
        return new ItemDtoWithBookings(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequest() == null ? null : item.getRequest().getId(),
                lastBooking == null ? null : BookingDtoMapper.bookingToDtoForItem(lastBooking),
                nextBooking == null ? null : BookingDtoMapper.bookingToDtoForItem(nextBooking),
                comments.stream().map(CommentDtoMapper::commentToDto).collect(Collectors.toList()));
    }
}
