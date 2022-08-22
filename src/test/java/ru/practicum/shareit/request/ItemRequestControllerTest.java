package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponse;
import ru.practicum.shareit.request.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

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
    void addRequestEndpointTest() throws Exception {
        when(itemRequestService.addRequest(user.getId(), ItemRequestDtoMapper.itemRequestToDto(itemRequest)))
                .thenReturn(ItemRequestDtoMapper.itemRequestToDto(itemRequest));

        mockMvc.perform(post("/requests").content(mapper.writeValueAsString(
                                ItemRequestDtoMapper.itemRequestToDto(itemRequest)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpectAll(status().isOk()
                        , content().json(mapper.writeValueAsString(ItemRequestDtoMapper.itemRequestToDto(itemRequest))));
    }

    @Test
    void getUserRequestsTest() throws Exception {
        List<ItemRequestDtoWithResponse> requests = List.of(ItemRequestDtoMapper
                .itemRequestToDtoWithResponse(itemRequest, List.of(ItemDtoMapper.ItemToDto(item))));

        when(itemRequestService.getUserRequests(user.getId()))
                .thenReturn(requests);

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", user.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(requests)));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        List<ItemRequestDtoWithResponse> requests = List.of(ItemRequestDtoMapper
                .itemRequestToDtoWithResponse(itemRequest, List.of(ItemDtoMapper.ItemToDto(item))));

        when(itemRequestService.getAllRequests(Optional.of(0), Optional.of(1), user.getId()))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all").param("from", "0")
                        .param("size", "1").header("X-Sharer-User-Id", user.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(requests)));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        ItemRequestDtoWithResponse request = ItemRequestDtoMapper
                .itemRequestToDtoWithResponse(itemRequest, List.of(ItemDtoMapper.ItemToDto(item)));

        when(itemRequestService.getRequestById(user.getId(), itemRequest.getId())).thenReturn(request);

        mockMvc.perform(get("/requests/{requestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(request)));
    }

    @Test
    void ItemRequestExceptionHandlerValidationExceptionTest() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenThrow(ValidationException.class);
        mockMvc.perform(get("/requests/15").header("X-Sharer-User-Id", 10))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void ItemRequestExceptionHandlerItemRequestNotFoundExceptionTest() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenThrow(new ItemRequestNotFoundException("message", Map.of("Id", "10")));
        mockMvc.perform(get("/requests/15").header("X-Sharer-User-Id", 10))
                .andExpectAll(status().isNotFound());
    }

    @Test
    void ItemRequestExceptionHandlerRuntimeExceptionTest() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenThrow(RuntimeException.class);
        mockMvc.perform(get("/requests/15").header("X-Sharer-User-Id", 10))
                .andExpectAll(status().isInternalServerError());
    }
}
