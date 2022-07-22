package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Item {
    //уникальный̆ идентификатор вещи
    private long id;

    //краткое название
    @NotBlank
    private String name;

    //развёрнутое описание
    @NotBlank
    private String description;

    //статус о том, доступна или нет вещь для аренды
    private boolean available;

    //владелец вещи
    private long owner_id;

    //если вещь была создана по запросу другого пользователя,
    //то в этом поле будет храниться ссылка на соответствующий запрос
    private long request_id;
}
