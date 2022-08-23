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
        User user = new User(200L, "user name 200", "email@200.com");
        user = userRepository.save(user);
        Item item = new Item(300L, "item name 300", "description 300",
                true, user, null);
        item = itemRepository.save(item);
        comment = new Comment(33L, "text 150", item, user, LocalDateTime.now());
        comment = commentRepository.save(comment);
    }

    @Test
    void findAllByItemIdTest() {

        List<Comment> comments = commentRepository.findAllByItemId(comment.getItem().getId());
        assertThat(comments).isEqualTo(List.of(comment));
    }
}
