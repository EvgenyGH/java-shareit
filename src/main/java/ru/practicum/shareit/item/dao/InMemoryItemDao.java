package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Map<Long, Item>> items = new HashMap<>();
    private long id = 0;
    private final UserService userService;

    //Добавление новой вещи
    @Override
    public Item addItem(ItemDto itemDto, long userId) {
        userService.getUserById(userId);

        itemDto.setId(++id);
        Item item = ItemDtoMapper.dtoToItem(itemDto, userId);

        if (items.containsKey(userId)) {
            Map<Long, Item> userItems = items.get(userId);
            userItems.put(id, item);
        } else {
            Map<Long, Item> userItems = new HashMap<>();
            userItems.put(id, item);
            items.put(userId, userItems);
        }

        log.trace("Item id={} добавлен: {}", id, item);

        return item;
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    @Override
    public Item updateItem(ItemDto itemDto, long userId, long itemId) {
        userService.getUserById(userId);

        if (!items.containsKey(userId) || !items.get(userId).containsKey(itemId)) {
            throw new ItemNotFoundException(String.format("Вещь id=%d у пользователя id=%d не найдена"
                    , itemId, userId)
                    , Map.of("Object", "Item"
                    , "ItemId", String.valueOf(itemId)
                    , "UserId", String.valueOf(userId)
                    , "Description", "Item not found"));
        }

        Item item = ItemDtoMapper.dtoToItem(itemDto, userId);
        Map<Long, Item> userItems = items.get(userId);
        Item tempItem = userItems.get(itemId);

        if (itemDto.getDescription() == null){
            item.setDescription(tempItem.getDescription());
        }

        if (itemDto.getName() == null){
            item.setName(tempItem.getName());
        }

        if (itemDto.getAvailable() == null){
            item.setAvailable(tempItem.isAvailable());
        }

        if (itemDto.getRequest_id() == null){
            item.setRequest_id(tempItem.getRequest_id());
        }

        validateItem(item);

        userItems.put(itemId, item);

        log.trace("Item id={} обновлен: {}", itemId, item);

        return item;
    }

    private void validateItem(@Valid Item item){

    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    @Override
    public Item getItemById(long itemId) {
        Optional<Map<Long,Item>> itemsFoundOpt = items.values().stream()
                .filter(userItems-> userItems.containsKey(itemId)).findFirst();

        if (itemsFoundOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Вещь id=%d не найдена"
                    , itemId)
                    , Map.of("Object", "Item"
                    , "ItemId", String.valueOf(itemId)
                    , "Description", "Item not found"));
        }

        Item item = itemsFoundOpt.get().get(itemId);

        log.trace("Item id={} отправлен: {}", itemId, item);

        return item;
    }

    //Просмотр владельцем списка всех его вещей.
    @Override
    public Collection<Item> getAllUserItems(long userId) {
        if (!items.containsKey(userId)) {
            throw new ItemNotFoundException(String.format("Вещи у пользователя id=%d не найдены"
                    , userId)
                    , Map.of("Object", "Item"
                    , "UserId", String.valueOf(userId)
                    , "Description", "Items not found"));
        }

        Collection<Item> userItems = items.get(userId).values();

        log.trace("Все {} Items пользователя id={} отправлены.", userItems.size(), userId);

        return userItems;
    }

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    @Override
    public List<Item> findItems(String text) {
        List<Item> itemsFound = items.values().stream().flatMap(userItems->userItems.values().stream())
                .filter((item->item.getName().contains(text) || item.getDescription().contains(text)
                && item.isAvailable())).collect(Collectors.toList());

        log.trace("Найдено {} Items содержащих <{}>.", itemsFound.size(), text);

        return itemsFound;
    }

    //Удалить вещи пользователя (при удалении пользователя)
    public void deleteUserItems (long userId){
        items.remove(userId);
        log.trace("Вещи пользователя {} удалены.", userId);
    }
}
