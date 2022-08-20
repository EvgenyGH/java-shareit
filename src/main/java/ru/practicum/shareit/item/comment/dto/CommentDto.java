package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    //уникальный идентификатор комментария
    private Long id;

    //содержимое комментария
    @NotBlank
    private String text;

    //вещь, к которой относится комментарии
    private String itemName;

    //author — автор комментария
    private String authorName;

    //дата создания комментария
    private LocalDateTime created;
}
