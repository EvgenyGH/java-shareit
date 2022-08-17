package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDtoWithResponse {
    //уникальный идентификатор запроса
    private Long id;

    //текст запроса, содержащий описание требуемой вещи
    @NotBlank
    private String description;

    //дата и время создания запроса
    private LocalDateTime created;

    //список ответов
    private List<ItemDto> items;
}
