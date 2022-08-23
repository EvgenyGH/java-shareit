package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponse;
import ru.practicum.shareit.request.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RequestServiceImplUnitTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void initialize() {
        user = new User(10L, "user name 10", "email@10.com");
        itemRequest = new ItemRequest(20L, "description 20", user, LocalDateTime.now());
        item = new Item(15L, "item name 15", "description 15", true, user, itemRequest);
    }

    @Test
    void addRequestTest() {
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto itemRequestDtoTest = itemRequestService.addRequest(user.getId()
                , ItemRequestDtoMapper.itemRequestToDto(itemRequest));

        assertThat(itemRequestDtoTest).isEqualTo(ItemRequestDtoMapper.itemRequestToDto(itemRequest));
    }

    @Test
    void getUserRequestsTest() {
        when(itemRequestRepository.findAllByRequestorId(user.getId())).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(item));

        List<ItemRequestDtoWithResponse> requestList = itemRequestService.getUserRequests(user.getId());

        assertThat(List.of(ItemRequestDtoMapper.itemRequestToDtoWithResponse(itemRequest
                , List.of(ItemDtoMapper.ItemToDto(item))))).isEqualTo(requestList);
    }

    @Test
    void getAllRequestsTest() {
        when(itemRequestRepository.findAllNotOwner(user.getId(), PageRequest.of(0, 10)))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(item));

        List<ItemRequestDtoWithResponse> requestList = itemRequestService.getAllRequests(
                Optional.of(0), Optional.of(10), user.getId());

        assertThat(List.of(ItemRequestDtoMapper.itemRequestToDtoWithResponse(itemRequest
                , List.of(ItemDtoMapper.ItemToDto(item))))).isEqualTo(requestList);
    }

    @Test
    void getAllRequestsFromNullTest() {
        List<ItemRequestDtoWithResponse> requestList = itemRequestService.getAllRequests(
                Optional.empty(), Optional.of(10), user.getId());
        assertThat(requestList.size()).isEqualTo(0);
    }

    @Test
    void getAllRequestsSizeNullTest() {
        List<ItemRequestDtoWithResponse> requestList = itemRequestService.getAllRequests(
                Optional.of(0), Optional.empty(), user.getId());
        assertThat(requestList.size()).isEqualTo(0);
    }

    @Test
    void getAllRequestsFromLessZeroTest() {
        assertThatThrownBy(() -> itemRequestService.getAllRequests(
                Optional.of(-1), Optional.of(10), user.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getAllRequestsSizeLessOneTest() {
        assertThatThrownBy(() -> itemRequestService.getAllRequests(
                Optional.of(0), Optional.of(0), user.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getRequestByIdTest() {
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(item));

        ItemRequestDtoWithResponse requestDto = itemRequestService.getRequestById(user.getId(), itemRequest.getId());

        assertThat(ItemRequestDtoMapper.itemRequestToDtoWithResponse(itemRequest
                , List.of(ItemDtoMapper.ItemToDto(item)))).isEqualTo(requestDto);
    }

    @Test
    void getItemRequestById() {
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemRequest request = itemRequestService.getItemRequestById(itemRequest.getId());

        assertThat(request).isEqualTo(itemRequest);
    }

    @Test
    void getItemRequestByIdThrowsItemRequestNotFoundException() {
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getItemRequestById(itemRequest.getId()))
                .isInstanceOf(ItemRequestNotFoundException.class);
    }
}
