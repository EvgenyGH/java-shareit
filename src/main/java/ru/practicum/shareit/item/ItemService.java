package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemDao itemDao;

    //Добавление новой вещи
    public Item addItem(ItemDto itemDto, long userId) {
        return itemDao.addItem(itemDto, userId);
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    public Item updateItem(ItemDto itemDto, long userId, long itemId) {
        return itemDao.updateItem(itemDto, userId, itemId);
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    public Item getItemById(long itemId) {
        return itemDao.getItemById(itemId);
    }

    //Просмотр владельцем списка всех его вещей.
    public Collection<Item> getAllUserItems(long userId) {
        return itemDao.getAllUserItems(userId);
    }

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    public List<Item> findItems(String text) {
        return itemDao.findItems(text);
    }

    //Удалить вещи пользователя (при удалении пользователя)
    public void deleteUserItems (long userId){
        itemDao.deleteUserItems(userId);
    }
}
