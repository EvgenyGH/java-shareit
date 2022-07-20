package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    //уникальный идентификатор пользователя
    private long id;

    //имя или логин пользователя
    private String name;
}
