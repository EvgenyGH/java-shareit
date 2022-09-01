package ru.practicum.shareitserver.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareitserver.booking.dto.BookingDtoMapper;
import ru.practicum.shareitserver.booking.dto.BookingDtoRequest;
import ru.practicum.shareitserver.booking.dto.BookingDtoResponse;
import ru.practicum.shareitserver.booking.exception.*;
import ru.practicum.shareitserver.booking.service.BookingService;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.item.exception.ItemNotFoundException;
import ru.practicum.shareitserver.user.User;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    @MockBean
    private final BookingService bookingService;

    private final MockMvc mockMvc;

    private final ObjectMapper mapper;

    private User userFirst;
    private User userSecond;
    private Booking bookingFirst;
    private Booking bookingSecond;
    private BookingDtoRequest dtoRequestFirst;

    @BeforeEach
    void initialization() {
        userFirst = new User(25L, "name 25", "email@25.com");
        userSecond = new User(35L, "name 35", "email@35.com");

        Item item = new Item(105L, "name 105", "description 105",
                true, userFirst, null);

        bookingFirst = new Booking(115L, LocalDateTime.now().plusDays(15),
                LocalDateTime.now().plusDays(20), item, userSecond, Status.APPROVED);
        bookingSecond = new Booking(125L, LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6), item, userSecond, Status.APPROVED);

        dtoRequestFirst = new BookingDtoRequest(bookingFirst.getStartDate(), bookingFirst.getEndDate(),
                bookingFirst.getItem().getId());
    }

    @Test
    void bookItemEndpointTest() throws Exception {
        when(bookingService.bookItem(dtoRequestFirst, bookingFirst.getBooker().getId()))
                .thenReturn(BookingDtoMapper.bookingToDto(bookingFirst));

        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dtoRequestFirst))
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpectAll(status().isOk(),
                        content().json(mapper.writeValueAsString(BookingDtoMapper.bookingToDto(bookingFirst))));
    }

    @Test
    void approveBookingEndpointTest() throws Exception {
        BookingDtoResponse dtoResponseTest = BookingDtoMapper.bookingToDto(bookingFirst);
        dtoResponseTest.setStatus(Status.REJECTED);

        when(bookingService.approveBooking(bookingFirst.getItem().getOwner().getId(),
                bookingFirst.getId(), false)).thenReturn(dtoResponseTest);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "false")
                        .header("X-Sharer-User-Id", bookingFirst.getItem().getOwner().getId()))
                .andExpectAll(status().isOk(), jsonPath("$.status").value(Status.REJECTED.toString()));
    }

    @Test
    void getBookingEndpointTest() throws Exception {
        when(bookingService.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenReturn(BookingDtoMapper.bookingToDto(bookingFirst));

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpectAll(status().isOk(),
                        content().json(mapper.writeValueAsString(BookingDtoMapper.bookingToDto(bookingFirst))));
    }

    @Test
    void getUserBookingByStatusAllEndpointTest() throws Exception {
        when(bookingService.getUserBookingByStatus(userSecond.getId(), RequestStatus.ALL, 0, 10))
                .thenReturn(Stream.of(bookingFirst, bookingSecond)
                        .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpectAll(status().isOk(),
                        content().json(mapper.writeValueAsString(Stream.of(bookingFirst, bookingSecond)
                                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()))));

    }

    @Test
    void getAllUserBookingsAllEndpointTest() throws Exception {
        when(bookingService.getAllUserBookings(userFirst.getId(), RequestStatus.ALL, 0, 10))
                .thenReturn(Stream.of(bookingFirst, bookingSecond)
                        .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userFirst.getId()))
                .andExpectAll(status().isOk(),
                        content().json(mapper.writeValueAsString(Stream.of(bookingFirst, bookingSecond)
                                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()))));
    }

    @Test
    void bookingExceptionHandlerMethodArgumentTypeMismatchExceptionTest() throws Exception {
        when(bookingService.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookingExceptionHandlerStartAfterEndExceptionTest() throws Exception {
        when(bookingService.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(new StartAfterEndException("msg",
                        Map.of("start", LocalDateTime.now().toString(),
                                "end", LocalDateTime.now().plusDays(1).toString())));

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookingExceptionHandlerItemNotAvailableExceptionTest() throws Exception {
        when(bookingService.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(new ItemNotAvailableException("msg", Map.of("id", "1")));

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookingExceptionHandlerIllegalArgumentExceptionTest() throws Exception {
        when(bookingService.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(new IllegalArgumentException("msg", null));

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookingExceptionHandlerItemNotFoundExceptionTest() throws Exception {
        when(bookingService.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(new ItemNotFoundException("msg", Map.of("Id", "1")));

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookingExceptionHandlerBookingNotExistsExceptionTest() throws Exception {
        when(bookingService.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(new BookingNotExistsException("msg", Map.of("Id", "1")));

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookingExceptionHandlerUserNotOwnerExceptionTest() throws Exception {
        when(bookingService.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(new UserNotOwnerException("msg", Map.of("Id", "1")));

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookingExceptionHandlerBookingExceptionTest() throws Exception {
        when(bookingService.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(BookingException.class);

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookingExceptionHandlerRuntimeExceptionTest() throws Exception {
        when(bookingService.getBooking(bookingFirst.getBooker().getId(), bookingFirst.getId()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/bookings/{bookingId}", bookingFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingFirst.getBooker().getId()))
                .andExpect(status().isInternalServerError());
    }
}