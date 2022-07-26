package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    //уникальный идентификатор пользователя
    private long id;

    //имя или логин пользователя
    @NotBlank
    private String name;

    //адрес электронной почты (два пользователя не могут иметь одинаковый адрес электронной почты).
    @Email
    private String email;
}
