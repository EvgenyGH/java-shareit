package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.exception.ItemNotRentedException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private Item item;
    private User user;

    @BeforeEach
    void initialize() {
        user = new User(10L, "user name 10", "email@10.com");
        item = new Item(15L, "item name 15", "description 15",
                true, user, null);
    }

    @Test
    void addItemEndpointTest() throws Exception {
        when(itemService.addItem(ItemDtoMapper.itemToDto(item), user.getId()))
                .thenReturn(item);
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ItemDtoMapper.itemToDto(item)))
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpectAll(status().isCreated(),
                        content().json(mapper.writeValueAsString(ItemDtoMapper.itemToDto(item))));
    }

    @Test
    void updateItemTest() throws Exception {
        item.setName("new name");
        when(itemService.updateItem(ItemDtoMapper.itemToDto(item), user.getId(), item.getId()))
                .thenReturn(item);
        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ItemDtoMapper.itemToDto(item)))
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpectAll(status().isOk(),
                        content().json(mapper.writeValueAsString(ItemDtoMapper.itemToDto(item))));
    }

    @Test
    void getItemByIdEndpointTest() throws Exception {
        when(itemService.getItemDtoWithBookingsById(item.getId(), user.getId()))
                .thenReturn(ItemDtoMapper.itemToDtoWithBookings(item,
                        null, null, Collections.emptyList()));
        mockMvc.perform(get("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpectAll(status().isOk(),
                        content().json(mapper.writeValueAsString(ItemDtoMapper.itemToDtoWithBookings(item,
                                null, null, Collections.emptyList()))));
    }

    @Test
    void getAllUserItemsEndpointTest() throws Exception {
        when(itemService.getAllUserItems(user.getId(), 0, 10))
                .thenReturn(List.of(ItemDtoMapper.itemToDtoWithBookings(item,
                        null, null, Collections.emptyList())));
        mockMvc.perform(get("/items").header("X-Sharer-User-Id", user.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(
                        List.of(ItemDtoMapper.itemToDtoWithBookings(item,
                                null, null, Collections.emptyList())))));
    }

    @Test
    void findItemsEndpointTest() throws Exception {
        when(itemService.findItems("text", 0, 10))
                .thenReturn(List.of(item));
        mockMvc.perform(get("/items/search").param("text", "text")
                .param("from", "0")
                .param("size", "10")
        ).andExpectAll(status().isOk(),
                content().json(mapper.writeValueAsString(List.of(ItemDtoMapper.itemToDto(item)))));
    }

    @Test
    void addCommentToItemEndpointTest() throws Exception {
        Comment comment = new Comment(1L, "text", item, user,
                LocalDateTime.of(1999, 12, 12, 0, 0));

        when(itemService.addCommentToItem(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                .header("X-Sharer-User-Id", user.getId())
                .content(mapper.writeValueAsString(comment))
                .contentType(MediaType.APPLICATION_JSON)).andExpectAll(
                status().isOk(), content().json(
                        mapper.writeValueAsString(CommentDtoMapper.commentToDto(comment))));
    }

    @Test
    void itemExceptionHandlerValidationExceptionTest() throws Exception {
        when(itemService.findItems("text", 0, 10))
                .thenThrow(ValidationException.class);
        mockMvc.perform(get("/items/search").param("text", "text"))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void itemExceptionHandlerUserNotFoundExceptionTest() throws Exception {
        when(itemService.findItems("text", 0, 10))
                .thenThrow(new UserNotFoundException("message", Map.of("Id", "1")));
        mockMvc.perform(get("/items/search").param("text", "text"))
                .andExpectAll(status().isNotFound());
    }

    @Test
    void itemExceptionHandlerItemNotFoundExceptionTest() throws Exception {
        when(itemService.findItems("text", 0, 10))
                .thenThrow(new ItemNotFoundException("message", Map.of("Id", "1")));
        mockMvc.perform(get("/items/search").param("text", "text"))
                .andExpectAll(status().isNotFound());
    }

    @Test
    void itemExceptionHandlerItemNotRentedExceptionTest() throws Exception {
        when(itemService.findItems("text", 0, 10))
                .thenThrow(new ItemNotRentedException("message",
                        Map.of("ItemId", "1", "UserId", "1")));
        mockMvc.perform(get("/items/search").param("text", "text"))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void itemExceptionHandlerRuntimeExceptionTest() throws Exception {
        when(itemService.findItems("text", 0, 10))
                .thenThrow(RuntimeException.class);
        mockMvc.perform(get("/items/search").param("text", "text"))
                .andExpectAll(status().isInternalServerError());
    }
}