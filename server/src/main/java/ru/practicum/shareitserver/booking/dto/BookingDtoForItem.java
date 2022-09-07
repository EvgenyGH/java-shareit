package ru.practicum.shareitserver.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareitserver.booking.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoForItem {
    //уникальный идентификатор бронирования
    private Long id;

    //дата начала бронирования
    private LocalDateTime start;

    //дата конца бронирования
    private LocalDateTime end;

    //id заказчика
    private Long bookerId;

    //статус бронирования.
    private Status status;
}
