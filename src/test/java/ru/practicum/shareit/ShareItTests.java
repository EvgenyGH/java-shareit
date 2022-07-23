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
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class ShareItTests {
    // TODO: 21.07.2022 добавить тесты
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Test
    void addGetGetAllUpdateFindItemTests1() throws Exception {
        User user1 = new User(1, "User name", "User@mail.ru");
        Item item1 = new Item(1L, "Item1 name", "Item1 description"
                , true, 1L, null);
        Item item2 = new Item(2L, "Item2 name", "Item2 description"
                , true, 1L, null);

        // TODO: 23.07.2022 delete user after tests
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isCreated(), content().json(objectMapper.writeValueAsString(user1)));

        //Добавление новой вещи
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(ItemDtoMapper.ItemToDto(item1)))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpectAll(status().isCreated()
                        , content().json(objectMapper.writeValueAsString(ItemDtoMapper.ItemToDto(item1))));


		/*mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(user))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());*/

        //Редактирование вещи. Редактировать вещь может только её владелец.

        //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.

        //Просмотр владельцем списка всех его вещей.

        //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
        //и система ищет вещи, содержащие этот текст в названии или описании.
        //Поиск возвращает только доступные для аренды вещи.
    }


}
