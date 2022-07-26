package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Booking {
    // WAITING — новое бронирование, ожидает одобрения,
    // APPROVED — бронирование подтверждено владельцем,
    // REJECTED — бронирование отклонено владельцем,
    // CANCELED — бронирование отменено создателем.
    public enum Status {WAITING, APPROVED, REJECTED, CANCELED}

    //уникальный идентификатор бронирования
    private long id;

    //дата начала бронирования
    private LocalDate start;

    //дата конца бронирования
    private LocalDate end;

    //вещь, которую пользователь бронирует
    private long item_id;

    //пользователь, который осуществляет бронирование;
    private long booker_id;

    //статус бронирования.
    private Status status;
}
