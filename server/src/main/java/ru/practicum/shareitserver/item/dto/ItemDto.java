package ru.practicum.shareitserver.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    //уникальный̆ идентификатор вещи
    private Long id;

    //краткое название
    @NotBlank
    private String name;

    //развёрнутое описание
    @NotBlank
    private String description;

    //статус о том, доступна или нет вещь для аренды
    @NotNull
    private Boolean available;

    //если вещь была создана по запросу другого пользователя,
    //то в этом поле будет храниться ссылка на соответствующий запрос
    private Long requestId;
}
