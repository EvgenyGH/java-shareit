package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class ShareItTests {
    // TODO: 21.07.2022 добавить тесты
    // TODO: 23.07.2022 readme
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Test
    void addGetGetAllUpdateFindItemTests1() throws Exception {
        User user1 = new User(1, "User name", "User@mail.ru");

        Item item1 = new Item(1L, "Item1 name", "Item1 description"
                , true, 1L, null);
        Item item2 = new Item(2L, "Item2 name", "Item2 description"
                , true, 1L, null);

        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isCreated(), content().json(objectMapper.writeValueAsString(user1)));

        //Добавление новой вещи
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(ItemDtoMapper.ItemToDto(item1)))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isCreated()
                        , content().json(objectMapper.writeValueAsString(ItemDtoMapper.ItemToDto(item1))));

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(ItemDtoMapper.ItemToDto(item2)))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isCreated()
                        , content().json(objectMapper.writeValueAsString(ItemDtoMapper.ItemToDto(item2))));

        item2.setName(" ");
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(ItemDtoMapper.ItemToDto(item2)))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
        item2.setName("Item2 name");

        item2.setDescription(" ");
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(ItemDtoMapper.ItemToDto(item2)))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
        item2.setDescription("Item2 description");

        ItemDto tempItemDto = ItemDtoMapper.ItemToDto(item2);
        tempItemDto.setAvailable(null);
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(tempItemDto))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());

        //Редактирование вещи. Редактировать вещь может только её владелец.
        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(ItemDtoMapper.ItemToDto(item2)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isNotFound());

        tempItemDto = ItemDtoMapper.ItemToDto(item1);
        tempItemDto.setName("updated name");
        tempItemDto.setDescription("updated description");
        tempItemDto.setAvailable(false);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(tempItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isOk()
                        , content().json(objectMapper.writeValueAsString(tempItemDto)));

        //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
        mockMvc.perform(get("/items/{itemId}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isOk()
                        , content().json(objectMapper.writeValueAsString(ItemDtoMapper.ItemToDto(item2))));


        //Просмотр владельцем списка всех его вещей.
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isOk()
                        , jsonPath("$.size()").value(2)
                        , jsonPath("$[0].id").value(1));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 2))
                .andExpectAll(status().isNotFound());

        //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
        //и система ищет вещи, содержащие этот текст в названии или описании.
        //Поиск возвращает только доступные для аренды вещи.
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "2"))
                .andExpectAll(status().isOk()
                        , content().json(objectMapper.writeValueAsString(List.of(ItemDtoMapper.ItemToDto(item2)))));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "deSCRiption"))
                .andExpectAll(status().isOk()
                        , jsonPath("$.size()").value(1));

        //Удаление пользователя по окончании теста
        mockMvc.perform(delete("/users/{userId}", 1)).andExpect(status().isOk());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isOk()
                        , jsonPath("$.size()").value(0));
    }
}
