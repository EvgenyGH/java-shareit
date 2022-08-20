package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    private ItemDto itemDto;

    private ItemDtoWithBookings itemDtoWithBookings;

    @Autowired
    private JacksonTester<ItemDto> jsonDto;

    @Autowired
    private JacksonTester<ItemDtoWithBookings> jsonDtoWithBooking;

    @BeforeEach
    void initialize() {
        User user = new User(10L, "name 10", "email@10.ru");

        ItemRequest itemRequest = new ItemRequest(5L, "description 5"
                , user, LocalDateTime.now().minusHours(1));

        Item item = new Item(15L, "name 15", "description 15", true
                , user, itemRequest);
        itemDto = ItemDtoMapper.ItemToDto(item);

        Booking bookingFirst = new Booking(2L, LocalDateTime.now().minusDays(2)
                , LocalDateTime.now().minusDays(1), item, user, Status.APPROVED);
        Booking bookingSecond = new Booking(3L, LocalDateTime.now().plusDays(1)
                , LocalDateTime.now().plusDays(2), item, user, Status.APPROVED);

        Comment comment = new Comment(3L, "text 3", item, user, LocalDateTime.now());

        itemDtoWithBookings = ItemDtoMapper.itemToDtoWithBookings(item, bookingFirst
                , bookingSecond, List.of(comment));
    }

    @Test
    void itemDtoWithBookingsJsonTest() throws IOException {
        JsonContent<ItemDtoWithBookings> json = jsonDtoWithBooking.write(itemDtoWithBookings);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDtoWithBookings.getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDtoWithBookings.getName());
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDtoWithBookings.getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDtoWithBookings.getAvailable());
        assertThat(json).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDtoWithBookings.getRequestId().intValue());
        assertThat(json).extractingJsonPathValue("$.comments.size()")
                .isEqualTo(itemDtoWithBookings.getComments().size());
        assertThat(json).extractingJsonPathValue("$.lastBooking.id")
                .isEqualTo(itemDtoWithBookings.getLastBooking().getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(itemDtoWithBookings.getLastBooking().getEnd().toString());
        assertThat(json).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(itemDtoWithBookings.getLastBooking().getStart().toString());
        assertThat(json).extractingJsonPathValue("$.lastBooking.status")
                .isEqualTo(itemDtoWithBookings.getLastBooking().getStatus().toString());
        assertThat(json).extractingJsonPathValue("$.lastBooking.bookerId")
                .isEqualTo(itemDtoWithBookings.getLastBooking().getBookerId().intValue());
        assertThat(json).extractingJsonPathValue("$.nextBooking.id")
                .isEqualTo(itemDtoWithBookings.getNextBooking().getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(itemDtoWithBookings.getNextBooking().getEnd().toString());
        assertThat(json).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(itemDtoWithBookings.getNextBooking().getStart().toString());
        assertThat(json).extractingJsonPathValue("$.nextBooking.status")
                .isEqualTo(itemDtoWithBookings.getNextBooking().getStatus().toString());
        assertThat(json).extractingJsonPathValue("$.nextBooking.bookerId")
                .isEqualTo(itemDtoWithBookings.getNextBooking().getBookerId().intValue());
    }

    @Test
    void itemDtoJsonTest() throws IOException {
        JsonContent<ItemDto> json = jsonDto.write(itemDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto.getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());
        assertThat(json).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId().intValue());
    }

}
