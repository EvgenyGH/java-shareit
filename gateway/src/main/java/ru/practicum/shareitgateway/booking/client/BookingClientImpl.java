package ru.practicum.shareitgateway.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BasicClient;
import ru.practicum.shareitserver.booking.dto.BookingDtoRequest;

import java.util.Map;

@Component
public class BookingClientImpl extends BasicClient implements BookingClient {
    @Autowired
    public BookingClientImpl(@Value("${shareit.server.url}") String url, RestTemplateBuilder builder) {
        super(builder
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .uriTemplateHandler(new DefaultUriBuilderFactory(url + "/bookings"))
                .build());
    }

    @Override
    public ResponseEntity<Object> bookItem(long userId, BookingDtoRequest bookingDtoRequest) {
        return post(userId, bookingDtoRequest);
    }

    @Override
    public ResponseEntity<Object> approveBooking(long userId, boolean approved, long bookingId) {
        Map<String, Object> params = Map.of("approved", approved);
        return patch(userId, "/" + bookingId + "?approved={approved}", null,
                params);
    }

    @Override
    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        return get(userId, "/" + bookingId);
    }

    @Override
    public ResponseEntity<Object> getUserBookingByStatus(long userId, String state, int from, int size) {
        Map<String, Object> params = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get(userId, "?state={state}&from={from}&size={size}", params);
    }

    @Override
    public ResponseEntity<Object> getAllUserBookings(long userId, String state, int from, int size) {
        Map<String, Object> params = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get(userId, "/owner?state={state}&from={from}&size={size}", params);
    }
}
