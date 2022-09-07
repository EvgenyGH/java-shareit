package ru.practicum.shareitserver.item.comment;

import lombok.*;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    //уникальный идентификатор комментария
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    //содержимое комментария
    private String text;

    //вещь, к которой относится комментарии
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    // author — автор комментария
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    //дата создания комментария
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
