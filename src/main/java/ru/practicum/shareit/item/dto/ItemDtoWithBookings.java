package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

@Getter
@Setter
@ToString
public class ItemDtoWithBookings extends ItemDto {
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;

    public ItemDtoWithBookings(Long id, String name, String description, Boolean available
            , Long requestId, BookingDtoForItem lastBooking, BookingDtoForItem nextBooking) {
        super(id, name, description, available, requestId);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
