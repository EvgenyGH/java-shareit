package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    //уникальный идентификатор пользователя
    private long id;

    //имя или логин пользователя
    private String name;

    //адрес электронной почты (два пользователя не могут иметь одинаковый адрес электронной почты).
    private String email;
}
