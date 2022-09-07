package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareitgateway.item.client.ItemClientImpl;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.item.comment.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.dto.ItemDtoMapper;
import ru.practicum.shareitserver.user.User;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"shareit.server.url=http://localhost:8080"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemClientImplTest {
    @SpyBean
    private final ItemClientImpl client;
    private static ResponseEntity<Object> response;
    private static User user;
    private static Item item;
    private static ItemDto itemDto;

    @BeforeAll
    static void initialize() {
        response = new ResponseEntity<>(HttpStatus.IM_USED);
        user = new User(1L, "name", "mail@mail.ru");
        item = new Item(1L, "item name", "description", true, user, null);
        itemDto = ItemDtoMapper.itemToDto(item);
    }

    @Test
    void whenAddItemThenCallPostWithUserIdAndItemDtoOnceAndReturnResponse() {
        doReturn(response).when(client).post(user.getId(), itemDto);
        assertThat(client.addItem(user.getId(), itemDto)).isEqualTo(response);
        verify(client, times(1)).post(user.getId(), itemDto);
    }

    @Test
    void whenUpdateItemThenCallPatchWithUserIdAndUrlAndItemDtoOnceAndReturnResponse() {
        doReturn(response).when(client).patch(user.getId(), "/" + item.getId(), itemDto);
        assertThat(client.updateItem(user.getId(), item.getId(), itemDto)).isEqualTo(response);
        verify(client, times(1)).patch(user.getId(), "/" + item.getId(), itemDto);
    }

    @Test
    void whenGetItemByIdThenCallGetWithUserIdAndUrlAndReturnResponse() {
        doReturn(response).when(client).get(user.getId(), "/" + item.getId());
        assertThat(client.getItemById(user.getId(), item.getId())).isEqualTo(response);
        verify(client, times(1)).get(user.getId(), "/" + item.getId());
    }

    @Test
    void whenGetAllUserItemsThenCallGetWithUserIdAndUrlAndParamsAndReturnResponse() {
        Map<String, Object> params = Map.of(
                "from", 10,
                "size", 20
        );
        doReturn(response).when(client).get(user.getId(), "?from={from}&size={size}", params);
        assertThat(client.getAllUserItems(user.getId(), 10, 20)).isEqualTo(response);
        verify(client, times(1)).get(user.getId(),
                "?from={from}&size={size}", params);
    }

    @Test
    void whenFindItemsThenCallGetWithUserIdAndUrlAndParamsAndReturnResponse() {
        Map<String, Object> params = Map.of(
                "from", 10,
                "size", 20,
                "text", "text"
        );
        doReturn(response).when(client)
                .get(null, "/search?text={text}&from={from}&size={size}", params);
        assertThat(client.findItems("text", 10, 20)).isEqualTo(response);
        verify(client, times(1))
                .get(null, "/search?text={text}&from={from}&size={size}", params);
    }

    @Test
    void whenAddCommentToItemThenCallPostWithUserIdAndUrlAndCommentDtoAndReturnResponse() {
        CommentDto comment = new CommentDto();

        doReturn(response).when(client)
                .post(user.getId(), "/" + item.getId() + "/comment", comment);
        assertThat(client.addCommentToItem(user.getId(), item.getId(), comment)).isEqualTo(response);
        verify(client, times(1))
                .post(user.getId(), "/" + item.getId() + "/comment", comment);
    }
}
