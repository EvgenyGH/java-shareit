package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;

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
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid ItemDto itemDto) {
        return ItemDtoMapper.ItemToDto(itemService.addItem(itemDto, userId));
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId
            , @Valid @RequestBody ItemDto itemDto) {
        return ItemDtoMapper.ItemToDto(itemService.updateItem(itemDto, userId, itemId));
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        return ItemDtoMapper.ItemToDto(itemService.getItemById(itemId));
    }

    //Просмотр владельцем списка всех его вещей.
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllUserItems(userId).stream().map(ItemDtoMapper::ItemToDto)
                .collect(Collectors.toList());
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
}
