package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
public class BookingDtoRequest {
    //дата начала бронирования
    @NotNull
    @FutureOrPresent
    private Date start;

    //дата конца бронирования
    @NotNull
    @FutureOrPresent
    private Date end;

    //вещь, которую пользователь бронирует
    @NotNull
    private Long itemId;
}

