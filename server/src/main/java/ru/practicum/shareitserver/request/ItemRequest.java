package ru.practicum.shareitserver.request;

import lombok.*;
import ru.practicum.shareitserver.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item_requests")
public class ItemRequest {
    //уникальный идентификатор запроса
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    //текст запроса, содержащий описание требуемой вещи
    private String description;

    //пользователь, создавший запрос
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;

    //дата и время создания запроса
    @Column(name = "created")
    private LocalDateTime createdDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemRequest that = (ItemRequest) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
