package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    //уникальный̆ идентификатор вещи
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    //владелец вещи
    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    //если вещь была создана по запросу другого пользователя,
    //то в этом поле будет храниться ссылка на соответствующий запрос
    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
