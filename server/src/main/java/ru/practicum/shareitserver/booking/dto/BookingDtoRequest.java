package ru.practicum.shareitserver.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoRequest {
    //дата начала бронирования
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    //дата конца бронирования
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;

    //вещь, которую пользователь бронирует
    @NotNull
    private Long itemId;
}

