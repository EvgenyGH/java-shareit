package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {
    //уникальный̆ идентификатор вещи
    private long id;

    //краткое название
    private String name;

    //развёрнутое описание
    private String description;

    //статус о том, доступна или нет вещь для аренды
    private boolean available;

    //если вещь была создана по запросу другого пользователя,
    //то в этом поле будет храниться ссылка на соответствующий запрос
    private long request_id;
}
