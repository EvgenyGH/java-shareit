package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequest {
    //уникальный идентификатор запроса
    private long id;

    //текст запроса, содержащий описание требуемой вещи
    private String description;

    //пользователь, создавший запрос
    private long requestor_id;

    //дата и время создания запроса
    private LocalDateTime created;
}
