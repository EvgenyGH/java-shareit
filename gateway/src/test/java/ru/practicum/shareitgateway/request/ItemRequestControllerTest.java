package ru.practicum.shareitgateway.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitgateway.request.client.ItemRequestClient;
import ru.practicum.shareitserver.request.ItemRequest;
import ru.practicum.shareitserver.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareitserver.user.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestClient client;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private User user;
    private ItemRequest itemRequest;
    private final ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.IM_USED);

    @BeforeEach
    void initialize() {
        user = new User(10L, "user name 10", "email@10.com");
        itemRequest = new ItemRequest(20L, "description 20", user, LocalDateTime.now());
    }

    @Test
    void addRequestEndpointTest() throws Exception {
        when(client.addRequest(user.getId(), ItemRequestDtoMapper.itemRequestToDto(itemRequest)))
                .thenReturn(response);

        mockMvc.perform(post("/requests").content(mapper.writeValueAsString(
                        ItemRequestDtoMapper.itemRequestToDto(itemRequest)))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", user.getId()));

        verify(client, times(1)).addRequest(user.getId(),
                ItemRequestDtoMapper.itemRequestToDto(itemRequest));
    }

    @Test
    void getUserRequestsTest() throws Exception {
        when(client.getUserRequests(user.getId())).thenReturn(response);

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", user.getId()));

        verify(client, times(1)).getUserRequests(user.getId());
    }

    @Test
    void getAllRequestsTest() throws Exception {
        when(client.getAllRequests(user.getId(), Optional.of(0), Optional.of(1)))
                .thenReturn(response);

        mockMvc.perform(get("/requests/all").param("from", "0")
                        .param("size", "1").header("X-Sharer-User-Id", user.getId()));

        verify(client, times(1))
                .getAllRequests(user.getId(), Optional.of(0), Optional.of(1));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(client.getRequestById(user.getId(), itemRequest.getId())).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", user.getId()));

        verify(client, times(1)).getRequestById(user.getId(), itemRequest.getId());
    }

    @Test
    void itemRequestExceptionHandlerValidationExceptionTest() throws Exception {
        when(client.getRequestById(anyLong(), anyLong())).thenThrow(ValidationException.class);
        mockMvc.perform(get("/requests/15").header("X-Sharer-User-Id", 10))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void itemRequestExceptionHandlerRuntimeExceptionTest() throws Exception {
        when(client.getRequestById(anyLong(), anyLong())).thenThrow(RuntimeException.class);
        mockMvc.perform(get("/requests/15").header("X-Sharer-User-Id", 10))
                .andExpectAll(status().isInternalServerError());
    }
}
