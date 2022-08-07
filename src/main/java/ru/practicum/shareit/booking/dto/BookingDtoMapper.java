package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoMapper;

public class BookingDtoMapper {
    public static BookingDtoResponse bookingToDto(Booking booking) {
        return new BookingDtoResponse(booking.getId(), booking.getStartDate()
                , booking.getEndDate(), ItemDtoMapper.ItemToDto(booking.getItem())
                , UserDtoMapper.userToDto(booking.getBooker()), booking.getStatus());
    }

    public static Booking DtoRequestToBooking(BookingDtoRequest bookingDtoRequest, Item item, User booker) {
        return new Booking(null, bookingDtoRequest.getStart(), bookingDtoRequest.getEnd()
                , item, booker, Status.WAITING);
    }

    public static BookingDtoForItem bookingToDtoForItem(Booking booking) {
        return new BookingDtoForItem(booking.getId(), booking.getStartDate()
                , booking.getEndDate(), booking.getBooker().getId()
                , booking.getStatus());
    }
}
