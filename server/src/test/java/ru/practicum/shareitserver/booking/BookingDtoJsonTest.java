package ru.practicum.shareitserver.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareitserver.booking.dto.BookingDtoForItem;
import ru.practicum.shareitserver.booking.dto.BookingDtoMapper;
import ru.practicum.shareitserver.booking.dto.BookingDtoRequest;
import ru.practicum.shareitserver.booking.dto.BookingDtoResponse;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.request.ItemRequest;
import ru.practicum.shareitserver.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingDtoResponse> jsonBookingDtoResponse;
    private final JacksonTester<BookingDtoRequest> jsonBookingDtoRequest;
    private final JacksonTester<BookingDtoForItem> jsonBookingDtoForItem;

    private BookingDtoResponse bookingDtoResponse;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoForItem bookingDtoForItem;

    @BeforeEach
    void initialize() {
        User user = new User(25L, "name 25", "email@25.com");
        ItemRequest itemRequest = new ItemRequest(40L, "description 40",
                user, LocalDateTime.now());
        Item item = new Item(105L, "name 105", "description 105",
                true, user, itemRequest);
        Booking booking = new Booking(115L, LocalDateTime.now().minusDays(15),
                LocalDateTime.now().minusDays(12), item, user, Status.APPROVED);

        bookingDtoResponse = BookingDtoMapper.bookingToDto(booking);
        bookingDtoForItem = BookingDtoMapper.bookingToDtoForItem(booking);
        bookingDtoRequest = new BookingDtoRequest(booking.getStartDate(), booking.getEndDate(),
                booking.getItem().getId());
    }

    @Test
    void bookingDtoRequestJsonTest() throws IOException {
        JsonContent<BookingDtoRequest> json = jsonBookingDtoRequest.write(bookingDtoRequest);

        assertThat(json).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoRequest.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDtoRequest.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingDtoRequest.getItemId().intValue());
    }

    @Test
    void bookingDtoResponseJsonTest() throws IOException {
        JsonContent<BookingDtoResponse> json = jsonBookingDtoResponse.write(bookingDtoResponse);

        assertThat(json).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoResponse.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDtoResponse.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingDtoResponse.getItem().getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingDtoResponse.getItem().getName());
        assertThat(json).extractingJsonPathStringValue("$.item.description")
                .isEqualTo(bookingDtoResponse.getItem().getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(bookingDtoResponse.getItem().getAvailable());
        assertThat(json).extractingJsonPathNumberValue("$.item.requestId")
                .isEqualTo(bookingDtoResponse.getItem().getRequestId().intValue());
        assertThat(json).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingDtoResponse.getBooker().getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(bookingDtoResponse.getBooker().getName());
        assertThat(json).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(bookingDtoResponse.getBooker().getEmail());
        assertThat(json).extractingJsonPathValue("$.status")
                .isEqualTo(bookingDtoResponse.getStatus().toString());
    }

    @Test
    void bookingDtoForItemJsonTest() throws IOException {
        JsonContent<BookingDtoForItem> json = jsonBookingDtoForItem.write(bookingDtoForItem);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDtoForItem.getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoForItem.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDtoForItem.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingDtoForItem.getBookerId().intValue());
        assertThat(json).extractingJsonPathValue("$.status")
                .isEqualTo(bookingDtoForItem.getStatus().toString());
    }
}
