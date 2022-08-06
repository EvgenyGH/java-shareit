package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor @NoArgsConstructor
@Entity
public class ItemRequest {
    //уникальный идентификатор запроса
    @Id
    private Long id;

    //текст запроса, содержащий описание требуемой вещи
    private String description;

    //пользователь, создавший запрос
    private long requestor_id;

    //дата и время создания запроса
    private LocalDateTime created;
}
