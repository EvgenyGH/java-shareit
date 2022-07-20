package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

public class BookingDtoMapper {
    public static BookingDto bookingToDto(Booking booking){
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem_id()
                , booking.getStatus());
    }
}
