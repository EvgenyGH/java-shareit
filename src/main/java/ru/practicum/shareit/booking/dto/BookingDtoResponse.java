package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
public class BookingDtoResponse {
    //уникальный идентификатор бронирования
    private Long id;

    //дата начала бронирования
    @NotNull
    private Date start;

    //дата конца бронирования
    @NotNull
    private Date end;

    //вещь, которую пользователь бронирует
    @NotNull
    private ItemDto item;

    private UserDto booker;

    //статус бронирования.
    private Status status;
}
