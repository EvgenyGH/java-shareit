package ru.practicum.shareitserver.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    //уникальный идентификатор запроса
    private Long id;

    //текст запроса, содержащий описание требуемой вещи
    @NotBlank
    private String description;

    //дата и время создания запроса
    private LocalDateTime created;
}
