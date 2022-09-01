package ru.practicum.shareitserver.booking.dto;

import ru.practicum.shareitserver.booking.Booking;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.item.dto.ItemDtoMapper;
import ru.practicum.shareitserver.user.User;
import ru.practicum.shareitserver.user.dto.UserDtoMapper;

public class BookingDtoMapper {
    public static BookingDtoResponse bookingToDto(Booking booking) {
        return new BookingDtoResponse(booking.getId(), booking.getStartDate(),
                booking.getEndDate(), ItemDtoMapper.itemToDto(booking.getItem()),
                UserDtoMapper.userToDto(booking.getBooker()), booking.getStatus());
    }

    public static Booking dtoRequestToBooking(BookingDtoRequest bookingDtoRequest, Item item, User booker) {
        return new Booking(null, bookingDtoRequest.getStart(), bookingDtoRequest.getEnd(),
                item, booker, Status.WAITING);
    }

    public static BookingDtoForItem bookingToDtoForItem(Booking booking) {
        return new BookingDtoForItem(booking.getId(), booking.getStartDate(),
                booking.getEndDate(), booking.getBooker().getId(),
                booking.getStatus());
    }
}
