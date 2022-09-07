package ru.practicum.shareitgateway.booking.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareitserver.booking.dto.BookingDtoRequest;

public interface BookingClient {
    ResponseEntity<Object> bookItem(long userId, BookingDtoRequest bookingDtoRequest);

    ResponseEntity<Object> approveBooking(long userId, boolean approved, long bookingId);

    ResponseEntity<Object> getBooking(long userId, long bookingId);

    ResponseEntity<Object> getUserBookingByStatus(long userId, String state, int from, int size);

    ResponseEntity<Object> getAllUserBookings(long userId, String state, int from, int size);
}
