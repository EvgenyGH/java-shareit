package ru.practicum.shareitgateway.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareitgateway.request.client.ItemRequestClientImpl;
import ru.practicum.shareitserver.request.ItemRequest;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareitserver.user.User;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"shareit.server.url=http://localhost:8080"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestClientImplTest {
    @SpyBean
    private final ItemRequestClientImpl client;
    private static ResponseEntity<Object> response;
    private static User user;
    private static ItemRequestDto itemRequestDto;
    private static ItemRequest itemRequest;

    @BeforeAll
    static void initialize() {
        response = new ResponseEntity<>(HttpStatus.IM_USED);
        user = new User(1L, "name", "mail@mail.ru");
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now());
        itemRequestDto = ItemRequestDtoMapper.itemRequestToDto(itemRequest);

    }

    @Test
    void whenAddRequestThenCallPostWithUserIdAndItemRequestDtoOnceAndReturnResponse() {
        doReturn(response).when(client).post(user.getId(), itemRequestDto);
        assertThat(client.addRequest(user.getId(), itemRequestDto)).isEqualTo(response);
        verify(client, times(1)).post(user.getId(), itemRequestDto);
    }

    @Test
    void whenGetUserRequestsThenCallGetWithUserIdOnceAndReturnResponse() {
        doReturn(response).when(client).get(user.getId());
        assertThat(client.getUserRequests(user.getId())).isEqualTo(response);
        verify(client, times(1)).get(user.getId());
    }

    @Test
    void whenGetAllRequestsThenCallGetWithUserIdAndUrlAndParamsOnceAndReturnResponse() {
        Map<String, Object> params = Map.of(
                "from", 10,
                "size", 20
        );
        doReturn(response).when(client).get(user.getId(), "/all?from={from}&size={size}", params);
        assertThat(client.getAllRequests(user.getId(), Optional.of(10), Optional.of(20)))
                .isEqualTo(response);
        verify(client, times(1))
                .get(user.getId(), "/all?from={from}&size={size}", params);
    }

    @Test
    void whenGetRequestByIdThenCallGetWithUserIdAndUrlOnceAndReturnResponse() {
        doReturn(response).when(client).get(user.getId(), "/" + itemRequest.getId());
        assertThat(client.getRequestById(user.getId(), itemRequest.getId()))
                .isEqualTo(response);
        verify(client, times(1))
                .get(user.getId(), "/" + itemRequest.getId());
    }
}