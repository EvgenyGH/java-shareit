package ru.practicum.shareitserver.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoResponse {
    //уникальный идентификатор бронирования
    private Long id;

    //дата начала бронирования
    @NotNull
    private LocalDateTime start;

    //дата конца бронирования
    @NotNull
    private LocalDateTime end;

    //вещь, которую пользователь бронирует
    @NotNull
    private ItemDto item;

    //заказчик
    private UserDto booker;

    //статус бронирования.
    private Status status;
}
