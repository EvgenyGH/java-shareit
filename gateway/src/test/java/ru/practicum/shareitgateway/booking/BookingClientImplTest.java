package ru.practicum.shareitgateway.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareitgateway.booking.client.BookingClientImpl;
import ru.practicum.shareitserver.booking.Booking;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.booking.dto.BookingDtoRequest;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.user.User;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"shareit.server.url=http://lcalhost:8080"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingClientImplTest {
    @SpyBean
    private final BookingClientImpl client;
    private static ResponseEntity<Object> response;
    private static User user;
    private static Booking booking;
    private static BookingDtoRequest bookingRequest;

    @BeforeAll
    static void initialize() {
        response = new ResponseEntity<>(HttpStatus.IM_USED);
        user = new User(1L, "name", "mail@mail.ru");
        Item item = new Item(1L, "item name", "description", true, user, null);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user, Status.APPROVED);
        bookingRequest = new BookingDtoRequest(booking.getStartDate(),
                booking.getEndDate(), booking.getItem().getId());
    }

    @Test
    void whenBookItemThenCallPostWithUserIdAndBookingDtoRequestOnceAndReturnResponse() {
        doReturn(response).when(client).post(user.getId(), bookingRequest);
        assertThat(client.bookItem(user.getId(), bookingRequest)).isEqualTo(response);
        verify(client, times(1)).post(user.getId(), bookingRequest);
    }

    @Test
    void whenApproveBookingThenCallPatchWithUserIdAndUrlAndParamsAndReturnResponse() {
        doReturn(response).when(client).patch(user.getId(),
                "/" + booking.getId() + "?approved={approved}", null,
                Map.of("approved", true));
        assertThat(client.approveBooking(user.getId(), true, booking.getId())).isEqualTo(response);
        verify(client, times(1)).patch(user.getId(),
                "/" + booking.getId() + "?approved={approved}", null,
                Map.of("approved", true));
    }

    @Test
    void whenGetBookingThenCallGetWithUserIdAndUrlOnceAndReturnResponse() {
        doReturn(response).when(client).get(user.getId(), "/" + booking.getId());
        assertThat(client.getBooking(user.getId(), booking.getId())).isEqualTo(response);
        verify(client, times(1)).get(user.getId(), "/" + booking.getId());
    }

    @Test
    void whenGetUserBookingByStatusThenCallGetWithUserIdAndUrlAndParamsOnceAndReturnResponse() {
        Map<String, Object> params = Map.of(
                "state", "ALL",
                "from", 10,
                "size", 20
        );

        doReturn(response).when(client).get(user.getId(), "?state={state}&from={from}&size={size}", params);
        assertThat(client.getUserBookingByStatus(user.getId(), "ALL", 10, 20))
                .isEqualTo(response);
        verify(client, times(1)).get(user.getId(),
                "?state={state}&from={from}&size={size}", params);
    }

    @Test
    void whenGetAllUserBookingsThenCallGetWithUserIdAndUrlAndParamsOnceAndReturnResponse() {
        Map<String, Object> params = Map.of(
                "state", "ALL",
                "from", 10,
                "size", 20
        );

        doReturn(response).when(client).get(user.getId(),
                "/owner?state={state}&from={from}&size={size}", params);
        assertThat(client.getAllUserBookings(user.getId(), "ALL", 10, 20))
                .isEqualTo(response);
        verify(client, times(1)).get(user.getId(),
                "/owner?state={state}&from={from}&size={size}", params);
    }
}
