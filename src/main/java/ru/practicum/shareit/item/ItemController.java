package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    //Добавление новой вещи
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        return ItemDtoMapper.ItemToDto(itemService.addItem(itemDto, userId));
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId
            , @RequestBody ItemDto itemDto) {
        return ItemDtoMapper.ItemToDto(itemService.updateItem(itemDto, userId, itemId));
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{itemId}")
    public ItemDtoWithBookings getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long itemId) {
        return itemService.getItemDtoWithBookingsById(itemId, userId);
    }

    //Просмотр владельцем списка всех его вещей.
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ItemDtoWithBookings> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllUserItems(userId);
    }

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam String text) {
        return itemService.findItems(text).stream().map(ItemDtoMapper::ItemToDto)
                .collect(Collectors.toList());
    }

    //Добавить комментарии
    //пользователь, который пишет комментарий, должен был брать вещь в аренду.
    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") long userId
            , @PathVariable long itemId, @RequestBody @Valid CommentDto commentDto) {
        return CommentDtoMapper.CommentToDto(itemService
                .addCommentToItem(userId, itemId, commentDto));
    }
}
