package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.List;

public interface ItemService {
    //Добавление новой вещи
    Item addItem(ItemDto itemDto, long userId);

    //Редактирование вещи. Редактировать вещь может только её владелец.
    Item updateItem(ItemDto itemDto, long userId, long itemId);

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    //Информация по датам аренды только для пользователя, который еще не арендовал вещ.
    ItemDtoWithBookings getItemDtoWithBookingsById(long itemId, long userId);

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    Item getItemById(long itemId);

    //Просмотр владельцем списка всех его вещей.
    List<ItemDtoWithBookings> getAllUserItems(long userId, int from, int size);

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    List<Item> findItems(String text, int from, int size);

    //Добавить комментарий
    Comment addCommentToItem(long userId, long itemId, CommentDto commentDto);
}
