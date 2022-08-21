package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponse;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class dtoAndDtoWithResponseJsonTest {
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoWithResponse itemRequestDtoWithResponse;

    @Autowired
    private JacksonTester<ItemRequestDto> jsonDto;

    @Autowired
    private JacksonTester<ItemRequestDtoWithResponse> jsonDtoWithResponse;

    @BeforeEach
    void initialize() {
        User user = new User(10L, "user name 10", "email@10.com");
        ItemRequest itemRequest = new ItemRequest(20L, "description 20", user, LocalDateTime.now());
        Item item = new Item(15L, "item name 15", "description 15", true, user, itemRequest);

        itemRequestDto = ItemRequestDtoMapper.itemRequestToDto(itemRequest);
        itemRequestDtoWithResponse = ItemRequestDtoMapper.itemRequestToDtoWithResponse(itemRequest
                , List.of(ItemDtoMapper.ItemToDto(item)));
    }

    @Test
    void itemRequestDtoJsonTest() throws IOException {
        JsonContent<ItemRequestDto> jsonContent = jsonDto.write(itemRequestDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestDto.getId().intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().toString());
    }

    @Test
    void itemRequestDtoWithResponseJsonTest() throws IOException {
        JsonContent<ItemRequestDtoWithResponse> jsonContent = jsonDtoWithResponse.write(itemRequestDtoWithResponse);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestDtoWithResponse.getId().intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDtoWithResponse.getDescription());
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDtoWithResponse.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(jsonContent).extractingJsonPathValue("$.items.size()")
                .isEqualTo(itemRequestDtoWithResponse.getItems().size());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemRequestDtoWithResponse.getItems().get(0).getId().intValue());
    }
}
