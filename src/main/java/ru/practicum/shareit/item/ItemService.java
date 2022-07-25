package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.UserService;

import javax.validation.*;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {
    private final ItemDao itemDao;
    private final UserService userService;
    private final Validator validator;

    @Autowired
    public ItemService(ItemDao itemDao, UserService userService) {
        this.itemDao = itemDao;
        this.userService = userService;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    //Добавление новой вещи
    public Item addItem(ItemDto itemDto, long userId) {
        userService.getUserById(userId);

        Item item = ItemDtoMapper.dtoToItem(itemDto, userId);
        itemDao.addItem(item);

        log.trace("Item id={} добавлен: {}", item.getId(), item);

        return item;
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    public Item updateItem(ItemDto itemDto, long userId, long itemId) {
        userService.getUserById(userId);

        Collection<Item> userItems = itemDao.getAllUserItems(userId);
        Item currentItem = userItems == null ? null
                : userItems.stream().filter(item -> item.getId() == itemId).findFirst().orElse(null);

        if (userItems != null && currentItem != null) {
            itemDto.setId(itemId);
            Item item = ItemDtoMapper.dtoToItem(itemDto, userId);

            if (itemDto.getDescription() == null) {
                item.setDescription(currentItem.getDescription());
            }

            if (itemDto.getName() == null) {
                item.setName(currentItem.getName());
            }

            if (itemDto.getAvailable() == null) {
                item.setAvailable(currentItem.isAvailable());
            }

            if (itemDto.getRequest_id() == null) {
                item.setRequest_id(currentItem.getRequest_id());
            }

            Set<ConstraintViolation<Item>> violations = validator.validate(item);
            if (violations.size() > 0) {
                throw new ConstraintViolationException(violations);
            }

            itemDao.updateItem(item);

            log.trace("Item id={} обновлен: {}", itemId, item);

            return item;
        } else {
            throw new ItemNotFoundException(String.format("Вещь id=%d у пользователя id=%d не найдена"
                    , itemId, userId)
                    , Map.of("Object", "Item"
                    , "ItemId", String.valueOf(itemId)
                    , "UserId", String.valueOf(userId)
                    , "Description", "Item not found"));
        }
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    public Item getItemById(long itemId) {
        Item item = itemDao.getItemById(itemId);

        if (item == null) {
            throw new ItemNotFoundException(String.format("Вещь id=%d не найдена"
                    , itemId)
                    , Map.of("Object", "Item"
                    , "Id", String.valueOf(itemId)
                    , "Description", "Item not found"));
        }

        log.trace("Item id={} отправлен: {}", itemId, item);

        return item;
    }

    //Просмотр владельцем списка всех его вещей.
    public Collection<Item> getAllUserItems(long userId) {
        Collection<Item> userItems = itemDao.getAllUserItems(userId);

        if (userItems == null) {
            throw new ItemNotFoundException(String.format("Вещи у пользователя id=%d не найдены"
                    , userId)
                    , Map.of("Object", "Item"
                    , "UserId", String.valueOf(userId)
                    , "Description", "Items not found"));
        }

        log.trace("Все {} Items пользователя id={} отправлены.", userItems.size(), userId);

        return userItems;
    }

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    public List<Item> findItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> itemsFound = itemDao.findItems(text.toLowerCase());

        log.trace("Найдено {} Items содержащих <{}>.", itemsFound.size(), text);

        return itemsFound;
    }

    //Удалить вещи пользователя (при удалении пользователя)
    public void deleteUserItems(long userId) {
        itemDao.deleteUserItems(userId);
        log.trace("Вещи пользователя {} удалены.", userId);
    }
}
