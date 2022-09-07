package ru.practicum.shareitgateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareitgateway.booking.client.BookingClient;
import ru.practicum.shareitserver.booking.Booking;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.booking.dto.BookingDtoRequest;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.user.User;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    @MockBean
    private final BookingClient client;
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private User userFirst;
    private User userSecond;
    private Booking bookingFirst;
    private BookingDtoRequest dtoRequestFirst;
    private final ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.IM_USED);

    @BeforeEach
    void initialization() {
        userFirst = new User(25L, "name 25", "email@25.com");
        userSecond = new User(35L, "name 35", "email@35.com");

        Item item = new Item(105L, "name 105", "description 105",
                true, userFirst, null);

        bookingFirst = new Booking(115L, LocalDateTime.now().plusDays(15),
                LocalDateTime.now().plusDays(20), item, userSecond, Status.APPROVED);

        dtoRequestFirst = new BookingDtoRequest(bookingFirst.getStartDate(), bookingFirst.getEndDate(),
                bookingFirst.getItem().getId());
    }

    @Test
    void bookItemEndpointTest() throws Exception {
        when(client.bookItem(bookingFirst.getBooker().getId(), dtoRequestFirst)).thenReturn(response);

        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoRequestFirst))
                .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()));

        verify(client, times(1)).bookItem(bookingFirst.getBooker().getId(), dtoRequestFirst);
    }

    @Test
    void approveBookingEndpointTest() throws Exception {
        when(client.approveBooking(bookingFirst.getItem().getOwner().getId(), false, bookingFirst.getId()))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "false")
                        .header("X-Sharer-User-Id", bookingFirst.getItem().getOwner().getId()));

        verify(client, times(1)).approveBooking(bookingFirst.getItem().getOwner().getId(),
                false, bookingFirst.getId());
    }

    @Test
    void getBookingEndpointTest() throws Exception {
        when(client.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()));

        verify(client, times(1))
                .getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId());
    }

    @Test
    void getUserBookingByStatusAllEndpointTest() throws Exception {
        when(client.getUserBookingByStatus(userSecond.getId(), "ALL", 0, 10))
                .thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()));

        verify(client,times(1))
                .getUserBookingByStatus(userSecond.getId(), "ALL", 0, 10);
    }

    @Test
    void getAllUserBookingsAllEndpointTest() throws Exception {
        when(client.getAllUserBookings(userFirst.getId(), "ALL", 0, 10))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userFirst.getId()));

        verify(client, times(1))
                .getAllUserBookings(userFirst.getId(), "ALL", 0, 10);
    }

    @Test
    void bookingExceptionHandlerMethodArgumentTypeMismatchExceptionTest() throws Exception {
        when(client.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookingExceptionHandlerRuntimeExceptionTest() throws Exception {
        when(client.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isInternalServerError());
    }
}