package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingDto {
    //уникальный идентификатор бронирования
    private Long bookingId;

    //дата начала бронирования
    private LocalDate start;

    //дата конца бронирования
    private LocalDate end;

    //вещь, которую пользователь бронирует
    private Long itemId;

    //статус бронирования.
    private Status status;
}
