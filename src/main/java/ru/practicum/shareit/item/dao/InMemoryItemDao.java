package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemDao implements ItemDao{
    private final Map<Long, HashMap<Long, Item>> items = new HashMap<>();

    //Добавление новой вещи
    @Override
    public Item addItem(ItemDto itemDto, long userId) {



        return null;
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    @Override
    public Item updateItem(ItemDto itemDto, long userId, long itemId) {
        return null;
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    @Override
    public Item getItemById(long itemId) {
        return null;
    }

    //Просмотр владельцем списка всех его вещей.
    @Override
    public List<Item> getAllUserItems(long userId) {
        return null;
    }

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    @Override
    public List<Item> findItems(String text) {
        return null;
    }
}
