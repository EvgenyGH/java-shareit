package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Booking.Status;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingDto {
    //уникальный идентификатор бронирования
    private long id;

    //дата начала бронирования
    private LocalDate start;

    //дата конца бронирования
    private LocalDate end;

    //вещь, которую пользователь бронирует
    private long item_id;

    //статус бронирования.
    private Status status;
}
