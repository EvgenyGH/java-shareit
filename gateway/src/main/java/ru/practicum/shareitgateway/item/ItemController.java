package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.item.comment.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.dto.ItemDtoWithBookings;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {


    //Добавление новой вещи
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        return null; //ItemDtoMapper.itemToDto(itemService.addItem(itemDto, userId));
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        return null; //ItemDtoMapper.itemToDto(itemService.updateItem(itemDto, userId, itemId));
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{itemId}")
    public ItemDtoWithBookings getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long itemId) {
        return null; //itemService.getItemDtoWithBookingsById(itemId, userId);
    }

    //Просмотр владельцем списка всех его вещей.
    //@ResponseStatus(HttpStatus.OK)
    @GetMapping
    public /*List<ItemDtoWithBookings>*/ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                               @RequestParam(required = false,
                                                             defaultValue = "0") @Min(0) int from,
                                                                               @RequestParam(required = false,
                                                             defaultValue = "10") @Min(1) int size) {
        return null;//client.get(userId, "http://localhost:9090/items", null,
                //Map.of()); //itemService.getAllUserItems(userId, from, size);
    }

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam String text,
                                   @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                   @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        return null; //itemService.findItems(text, from, size).stream().map(ItemDtoMapper::itemToDto)
                //.collect(Collectors.toList());
    }

    //Добавить комментарии
    //пользователь, который пишет комментарий, должен был брать вещь в аренду.
    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long itemId,
                                       @RequestBody @Valid CommentDto commentDto) {
        return null; //CommentDtoMapper.commentToDto(itemService
                //.addCommentToItem(userId, itemId, commentDto));
    }
}
