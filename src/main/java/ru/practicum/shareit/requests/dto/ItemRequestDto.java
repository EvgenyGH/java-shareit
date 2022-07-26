package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    //уникальный идентификатор запроса
    private long id;

    //текст запроса, содержащий описание требуемой вещи
    private String description;

    //дата и время создания запроса
    private LocalDateTime created;
}
