package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemDao {
    //Добавление новой вещи
    Item addItem(ItemDto itemDto, long userId);

    //Редактирование вещи. Редактировать вещь может только её владелец.
    Item updateItem(ItemDto itemDto, long userId, long itemId);

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    Item getItemById(long itemId);

    //Просмотр владельцем списка всех его вещей.
    Collection<Item> getAllUserItems(long userId);

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    List<Item> findItems(String text);

    //Удалить вещи пользователя (при удалении пользователя)
    void deleteUserItems(long userId);
}
