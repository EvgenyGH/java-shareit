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
import java.util.Optional;
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

    @BeforeEach
    void initialize() {
        user = userService.addUser(new User(10L, "user name 10", "email@10.com"));
        itemRequest = new ItemRequest(20L, "description 20", user, LocalDateTime.now());
        ItemRequestDto itemRequestDto = itemRequestService.addRequest(user.getId(), ItemRequestDtoMapper
                .itemRequestToDto(itemRequest));
        itemRequest.setId(itemRequestDto.getId());
        Item item = new Item(15L, "item name 15", "description 15", true, user, itemRequest);
        itemService.addItem(ItemDtoMapper.ItemToDto(item), user.getId());
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


        List<ItemRequestDtoWithResponse> requestDtoDb = requestsDb.stream().map(itemRequest ->
                        ItemRequestDtoMapper.itemRequestToDtoWithResponse(itemRequest
                                , items.stream().map(ItemDtoMapper::ItemToDto).collect(Collectors.toList())))
                .collect(Collectors.toList());

        assertThat(requestDtoDb.size()).isEqualTo(requests.size());
        assertThat(requestDtoDb.get(0).getId()).isEqualTo(requests.get(0).getId());
        assertThat(requestDtoDb.get(0).getDescription()).isEqualTo(requests.get(0).getDescription());
        assertThat(requestDtoDb.get(0).getItems()).isEqualTo(requests.get(0).getItems());
    }

    @Test
    @Transactional
    void getAllRequestsTest() {
        User userTest = userService.addUser(new User(15L, "user name new", "email@new.com"));
        ItemRequest itemRequestTest = new ItemRequest(20L, "description new", userTest
                , LocalDateTime.now());
        ItemRequestDto itemRequestDtoTest = itemRequestService.addRequest(userTest.getId(), ItemRequestDtoMapper
                .itemRequestToDto(itemRequestTest));
        itemRequestTest.setId(itemRequestDtoTest.getId());
        Item itemTest = new Item(15L, "item name new", "description new", true
                , userTest, itemRequestTest);
        itemService.addItem(ItemDtoMapper.ItemToDto(itemTest), userTest.getId());

        List<ItemRequestDtoWithResponse> allRequests = itemRequestService.getAllRequests(
                Optional.of(0), Optional.of(5), user.getId());

        TypedQuery<ItemRequest> queryRequest = entityManager.createQuery(
                "SELECT i FROM ItemRequest i " +
                        "WHERE i.requestor.id <> :id", ItemRequest.class);

        List<ItemRequest> requestsDb = queryRequest.setParameter("id", user.getId()).getResultList();

        TypedQuery<Item> queryItem = entityManager.createQuery(
                "SELECT i FROM Item i " +
                        "WHERE i.request.id = :id", Item.class);

        List<Item> items = queryItem.setParameter("id", itemRequestTest.getId()).getResultList();


        List<ItemRequestDtoWithResponse> requestDtoDb = requestsDb.stream().map(itemRequest ->
                        ItemRequestDtoMapper.itemRequestToDtoWithResponse(itemRequest
                                , items.stream().map(ItemDtoMapper::ItemToDto).collect(Collectors.toList())))
                .collect(Collectors.toList());

        assertThat(requestDtoDb.size()).isEqualTo(allRequests.size());
        assertThat(requestDtoDb.get(0).getId()).isEqualTo(allRequests.get(0).getId());
        assertThat(requestDtoDb.get(0).getDescription()).isEqualTo(allRequests.get(0).getDescription());
        assertThat(requestDtoDb.get(0).getItems()).isEqualTo(allRequests.get(0).getItems());
    }

    @Test
    @Transactional
    void getRequestByIdTest() {
        ItemRequestDtoWithResponse request = itemRequestService.getRequestById (user.getId(), itemRequest.getId());

        TypedQuery<ItemRequest> queryRequest = entityManager.createQuery(
                "SELECT i FROM ItemRequest i " +
                        "WHERE i.requestor.id = :id", ItemRequest.class);

        ItemRequest requestsDb = queryRequest.setParameter("id", user.getId()).getSingleResult();

        TypedQuery<Item> queryItem = entityManager.createQuery(
                "SELECT i FROM Item i " +
                        "WHERE i.request.id = :id", Item.class);

        List<Item> items = queryItem.setParameter("id", itemRequest.getId()).getResultList();

        ItemRequestDtoWithResponse requestDtoDb = ItemRequestDtoMapper.itemRequestToDtoWithResponse(requestsDb
                                , items.stream().map(ItemDtoMapper::ItemToDto).collect(Collectors.toList()));

        assertThat(requestDtoDb.getId()).isEqualTo(request.getId());
        assertThat(requestDtoDb.getDescription()).isEqualTo(request.getDescription());
        assertThat(requestDtoDb.getItems()).isEqualTo(request.getItems());
    }

    @Test
    @Transactional
    void getItemRequestByIdTest() {
        ItemRequest request = itemRequestService.getItemRequestById (itemRequest.getId());

        TypedQuery<ItemRequest> queryRequest = entityManager.createQuery(
                "SELECT i FROM ItemRequest i " +
                        "WHERE i.requestor.id = :id", ItemRequest.class);

        ItemRequest requestsDb = queryRequest.setParameter("id", user.getId()).getSingleResult();

        assertThat(request).isEqualTo(requestsDb);
    }
}

