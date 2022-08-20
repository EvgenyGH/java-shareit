package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentRepositoryTest {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private Comment comment;

    @BeforeEach
    void initialize() {
        User user = new User(10L, "user name 10", "email@10.com");
        user = userRepository.save(user);
        Item item = new Item(15L, "item name 15", "description 15"
                , true, user, null);
        item = itemRepository.save(item);
        comment = new Comment(33L, "text 33", item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);
    }

    @Test
    void findAllByItemIdTest() {
        List<Comment> comments = commentRepository.findAllByItemId(comment.getId());
        assertThat(comments).isEqualTo(List.of(comment));
    }
}
