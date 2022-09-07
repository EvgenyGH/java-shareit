package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.item.client.ItemClient;
import ru.practicum.shareitserver.item.comment.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class ItemController {
    private final ItemClient client;

    //Добавление новой вещи
    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.trace("Gateway: addItem: userId={}, itemDto={}", userId, itemDto);
        return client.addItem(userId, itemDto);
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.trace("Gateway: updateItem: userId={}, itemId={}, itemDto={}", userId, itemId, itemDto);
        return client.updateItem(userId, itemId, itemDto);
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        log.trace("Gateway: getItemById: userId={}, itemId={}", userId, itemId);
        return client.getItemById(userId, itemId);
    }

    //Просмотр владельцем списка всех его вещей.
    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(required = false,
                                                          defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(required = false,
                                                          defaultValue = "10") @Min(1) int size) {
        log.trace("Gateway: getAllUserItems: userId={}, from={}, size={}", userId, from, size);
        return client.getAllUserItems(userId, from, size);
    }

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    @GetMapping("/search")
    public ResponseEntity<Object> findItems(@RequestParam String text,
                                            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                            @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.trace("Gateway: findItems: text={}, from={}, size={}", text, from, size);
        return client.findItems(text, from, size);
    }

    //Добавить комментарии
    //пользователь, который пишет комментарий, должен был брать вещь в аренду.
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PathVariable long itemId,
                                                   @RequestBody @Valid CommentDto commentDto) {
        log.trace("Gateway: addCommentToItem: userId={}, itemId={}, commentDto={}", userId, itemId, commentDto);
        return client.addCommentToItem(userId, itemId, commentDto);
    }
}
