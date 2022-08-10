package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Getter
@Setter
@ToString
public class ItemDtoWithBookings extends ItemDto {
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private final List<CommentDto> comments;

    public ItemDtoWithBookings(Long id, String name, String description, Boolean available
            , Long requestId, BookingDtoForItem lastBooking, BookingDtoForItem nextBooking
            , List<CommentDto> comments) {
        super(id, name, description, available, requestId);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
