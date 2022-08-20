package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item;

    @BeforeEach
    void initialize() {
        user = new User(10L, "user name 10", "email@10.com");
        user = userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(22L, "description 22"
                , user, LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
        item = new Item(15L, "item name 15", "description 15"
                , true, user, itemRequest);
        item = itemRepository.save(item);
    }

    @Test
    void findByIdAndOwnerTest(){
        Item itemTest = itemRepository.findByIdAndOwner(item.getId(), user).orElse(null);
        assertThat(itemTest).isEqualTo(item);
    }

    @Test
    void findAllByOwnerOrderByIdTest(){
        List<Item> items = itemRepository.findAllByOwnerOrderById(user, PageRequest.of(0,1));
        assertThat(items).isEqualTo(List.of(item));
    }

    @Test
    void findAllByNameOrDescriptionIgnoreCase(){
        List<Item> items = itemRepository
                .findAllByNameOrDescriptionIgnoreCase("15", PageRequest.of(0, 10));
        assertThat(items).isEqualTo(List.of(item));
    }

    @Test
    void findAllByRequestId(){
        List<Item> items = itemRepository.findAllByRequestId(item.getRequest().getId());
        assertThat(items).isEqualTo(List.of(item));
    }
}