package ru.practicum.shareitgateway.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BasicClient;
import ru.practicum.shareitserver.item.comment.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.ItemDto;

import java.util.Map;

@Component
public class ItemClientImpl extends BasicClient implements ItemClient {
    @Autowired
    public ItemClientImpl(@Value("${SERVER_URL}") String url, RestTemplateBuilder builder) {
        super(builder
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .uriTemplateHandler(new DefaultUriBuilderFactory(url + "/items"))
                .build());
    }

    @Override
    public ResponseEntity<Object> addItem(long userId, ItemDto itemDto) {
        return post(userId, itemDto);
    }

    @Override
    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto itemDto) {
        return patch(userId, "/" + itemId, itemDto);
    }

    @Override
    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get(userId, "/" + itemId, null);
    }

    @Override
    public ResponseEntity<Object> getAllUserItems(long userId, int from, int size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size
        );
        return get(userId, "?from={from}&size={size}", params);
    }

    @Override
    public ResponseEntity<Object> findItems(String text, int from, int size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size,
                "text", text
        );
        return get(null, "/search?text={text}&from={from}&size={size}", params);
    }

    @Override
    public ResponseEntity<Object> addCommentToItem(long userId, long itemId, CommentDto commentDto) {
        return post(userId, "/" + itemId + "/comment", commentDto);
    }
}
