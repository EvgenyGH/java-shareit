package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {
    //уникальный идентификатор пользователя
    private long id;

    //имя или логин пользователя
    @NotBlank
    private String name;

    //адрес электронной почты
    @Email
    @NotBlank
    private String email;
}
