package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private User userFirst;
    private ItemRequest itemRequestFirst;
    private ItemRequest itemRequestSecond;

    @BeforeEach
    void initialize() {
        userFirst = new User(10L, "user name 10", "email@10.com");
        User userSecond = new User(15L, "user name 10", "email@10.com");
        userFirst = userRepository.save(userFirst);
        userSecond = userRepository.save(userSecond);
        itemRequestFirst = new ItemRequest(20L, "description First", userFirst, LocalDateTime.now());
        itemRequestSecond = new ItemRequest(40L, "description Second", userSecond, LocalDateTime.now());
        itemRequestFirst = itemRequestRepository.save(itemRequestFirst);
        itemRequestSecond = itemRequestRepository.save(itemRequestSecond);
        Item itemFirst = new Item(40L, "item name 40", "description 40", true, userFirst
                , itemRequestFirst);
        Item itemSecond = new Item(50L, "item name 50", "description 50", true, userSecond
                , itemRequestSecond);
        itemRepository.save(itemFirst);
        itemRepository.save(itemSecond);
    }

    @Test
    @Transactional
    void findAllByRequestorIdTest() {
        assertThat(itemRequestRepository.findAllByRequestorId(userFirst.getId())).isEqualTo(List.of(itemRequestFirst));
    }

    @Test
    @Transactional
    void findAllNotOwnerTest() {
        assertThat(itemRequestRepository.findAllNotOwner(userFirst.getId(), PageRequest.of(0, 10)))
                .isEqualTo(List.of(itemRequestSecond));
    }
}
