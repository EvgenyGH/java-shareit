package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationRequestServiceAndDbTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager entityManager;

    private User user;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void initialize() {
        user = userService.addUser(new User(10L, "user name 10", "email@10.com"));
        itemRequest = new ItemRequest(20L, "description 20", user, LocalDateTime.now());
        ItemRequestDto itemRequestDto = itemRequestService.addRequest(user.getId(), ItemRequestDtoMapper
                .itemRequestToDto(itemRequest));
        itemRequest.setId(itemRequestDto.getId());
        item = new Item(15L, "item name 15", "description 15", true, user, itemRequest);
        item = itemService.addItem(ItemDtoMapper.ItemToDto(item), user.getId());
    }

    @Test
    @Transactional
    void addRequestAndGetRequestTest() {
        ItemRequestDto itemRequestDto = itemRequestService.addRequest(user.getId()
                , ItemRequestDtoMapper.itemRequestToDto(itemRequest));

        assertThat(itemRequestService.getItemRequestById(itemRequestDto.getId()))
                .isEqualTo(ItemRequestDtoMapper.toItemRequest(itemRequestDto, user));
    }

    @Test
    @Transactional
    void getUserRequestsTest() {
        List<ItemRequestDtoWithResponse> requests = itemRequestService.getUserRequests(user.getId());

        TypedQuery<ItemRequest> queryRequest = entityManager.createQuery(
                "SELECT i FROM ItemRequest i " +
                        "WHERE i.requestor.id = :id", ItemRequest.class);

        List<ItemRequest> requestsDb = queryRequest.setParameter("id", user.getId()).getResultList();

        TypedQuery<Item> queryItem = entityManager.createQuery(
                "SELECT i FROM Item i " +
                        "WHERE i.request.id = :id", Item.class);

        List<Item> items = queryItem.setParameter("id", itemRequest.getId()).getResultList();


        List<ItemRequestDtoWithResponse> requestDtoDb = requestsDb.stream().map(ItemRequest ->
                        ItemRequestDtoMapper.itemRequestToDtoWithResponse(itemRequest
                                , items.stream().map(ItemDtoMapper::ItemToDto).collect(Collectors.toList())))
                        .collect(Collectors.toList());

        assertThat(requestDtoDb.size()).isEqualTo(requests.size());
        assertThat(requestDtoDb.get(0).getId()).isEqualTo(requests.get(0).getId());
        assertThat(requestDtoDb.get(0).getDescription()).isEqualTo(requests.get(0).getDescription());
        assertThat(requestDtoDb.get(0).getItems()).isEqualTo(requests.get(0).getItems());
    }


    /*//Получить список запросов, созданных другими пользователями.
    //Запросы сортируются от более новых к более старым.
    //Результаты возвращаются постранично.
    //from — индекс первого элемента, size — количество элементов для отображения.
    List<ItemRequestDtoWithResponse> getAllRequests(Optional<Integer> fromOpt, Optional<Integer> sizeOpt
            , Long ownerId);

    //Получить данные об одном конкретном запросе вместе с данными об ответах по id.
    ItemRequestDtoWithResponse getRequestById(Long userId, Long requestId);

    //Получить запрос по id.
    ItemRequest getItemRequestById(Long requestId);*/
}

