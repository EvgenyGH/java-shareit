package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Map<Long, Item>> items = new HashMap<>();

    //Добавление новой вещи
    @Override
    public Item addItem(Item item) {
        if (items.containsKey(item.getOwner_id())) {
            Map<Long, Item> userItems = items.get(item.getOwner_id());
            userItems.put(item.getId(), item);
        } else {
            Map<Long, Item> userItems = new HashMap<>();
            userItems.put(item.getId(), item);
            items.put(item.getOwner_id(), userItems);
        }

        return item;
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    @Override
    public Item updateItem(Item item) {
        items.get(item.getOwner_id()).put(item.getId(), item);

        return item;
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    @Override
    public Item getItemById(long itemId) {
        Item itemFound = items.values().stream()
                .filter(userItem -> userItem.containsKey(itemId))
                .flatMap(userItems -> userItems.values().stream())
                .findFirst().orElse(null);

        return itemFound;
    }

    //Просмотр владельцем списка всех его вещей.
    @Override
    public Collection<Item> getAllUserItems(long userId) {
        if (!items.containsKey(userId)) {
            return null;
        }

        return items.get(userId).values();
    }

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    @Override
    public List<Item> findItems(String text) {
        List<Item> itemsFound = items.values().stream().flatMap(userItems -> userItems.values().stream())
                .filter((item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text)
                        && item.isAvailable())).collect(Collectors.toList());

        return itemsFound;
    }

    //Удалить вещи пользователя (при удалении пользователя)
    public void deleteUserItems(long userId) {
        items.remove(userId);
    }
}
