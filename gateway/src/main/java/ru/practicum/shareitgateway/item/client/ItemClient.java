package ru.practicum.shareitgateway.item.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareitserver.item.comment.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.ItemDto;

public interface ItemClient {
    ResponseEntity<Object> addItem(long userId, ItemDto itemDto);

    ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto itemDto);

    ResponseEntity<Object> getItemById(long userId, long itemId);

    ResponseEntity<Object> getAllUserItems(long userId, int from, int size);

    ResponseEntity<Object> findItems(String text, int from, int size);

    ResponseEntity<Object> addCommentToItem(long userId, long itemId, CommentDto commentDto);
}
