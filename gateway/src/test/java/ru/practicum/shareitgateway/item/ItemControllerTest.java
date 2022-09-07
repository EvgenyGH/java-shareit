package ru.practicum.shareitgateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitgateway.item.client.ItemClient;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.item.comment.Comment;
import ru.practicum.shareitserver.item.comment.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.ItemDtoMapper;
import ru.practicum.shareitserver.user.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemClient client;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private Item item;
    private User user;
    private final ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.IM_USED);

    @BeforeEach
    void initialize() {
        user = new User(10L, "user name 10", "email@10.com");
        item = new Item(15L, "item name 15", "description 15",
                true, user, null);
    }

    @Test
    void addItemEndpointTest() throws Exception {
        when(client.addItem(user.getId(), ItemDtoMapper.itemToDto(item)))
                .thenReturn(response);
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ItemDtoMapper.itemToDto(item)))
                .header("X-Sharer-User-Id", user.getId()));

        verify(client, times(1)).addItem(user.getId(), ItemDtoMapper.itemToDto(item));
    }

    @Test
    void updateItemTest() throws Exception {
        when(client.updateItem(user.getId(), item.getId(), ItemDtoMapper.itemToDto(item)))
                .thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ItemDtoMapper.itemToDto(item)))
                .header("X-Sharer-User-Id", user.getId()));

        verify(client, times(1))
                .updateItem(user.getId(), item.getId(), ItemDtoMapper.itemToDto(item));
    }

    @Test
    void getItemByIdEndpointTest() throws Exception {
        when(client.getItemById(user.getId(), item.getId()))
                .thenReturn(response);
        mockMvc.perform(get("/items/{itemId}", item.getId())
                .header("X-Sharer-User-Id", user.getId()));

        verify(client, times(1)).getItemById(user.getId(), item.getId());
    }

    @Test
    void getAllUserItemsEndpointTest() throws Exception {
        when(client.getAllUserItems(user.getId(), 0, 10))
                .thenReturn(response);
        mockMvc.perform(get("/items").header("X-Sharer-User-Id", user.getId()));

        verify(client, times(1)).getAllUserItems(user.getId(), 0, 10);
    }

    @Test
    void findItemsEndpointTest() throws Exception {
        when(client.findItems("text", 0, 10)).thenReturn(response);

        mockMvc.perform(get("/items/search").param("text", "text")
                .param("from", "0")
                .param("size", "10"));

        verify(client, times(1)).findItems("text", 0, 10);
    }

    @Test
    void addCommentToItemEndpointTest() throws Exception {
        Comment comment = new Comment(1L, "text", item, user,
                LocalDateTime.of(1999, 12, 12, 0, 0));

        when(client.addCommentToItem(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                .header("X-Sharer-User-Id", user.getId())
                .content(mapper.writeValueAsString(comment))
                .contentType(MediaType.APPLICATION_JSON));

        verify(client, times(1))
                .addCommentToItem(anyLong(), anyLong(), any(CommentDto.class));
    }

    @Test
    void exceptionHandlerValidationExceptionTest() throws Exception {
        when(client.findItems("text", 0, 10))
                .thenThrow(ValidationException.class);
        mockMvc.perform(get("/items/search").param("text", "text"))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void exceptionHandlerRuntimeExceptionTest() throws Exception {
        when(client.findItems("text", 0, 10))
                .thenThrow(RuntimeException.class);
        mockMvc.perform(get("/items/search").param("text", "text"))
                .andExpectAll(status().isInternalServerError());
    }
}