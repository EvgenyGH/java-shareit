package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final Validator validator;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserService userService
            , BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    //Добавление новой вещи
    public Item addItem(ItemDto itemDto, long userId) {
        User user = userService.getUserById(userId);
        itemDto.setId(null);

        //Функционал ItemRequest будет реализован в следующем спринте, поэтому значение null
        Item item = ItemDtoMapper.dtoToItem(itemDto, user, null);
        item = itemRepository.save(item);

        log.trace("Item id={} добавлен: {}", item.getId(), item);

        return item;
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    public Item updateItem(ItemDto itemDto, long userId, long itemId) {
        User user = userService.getUserById(userId);

        Optional<Item> currentItemOpt = itemRepository.findByIdAndOwner(itemId, user);

        if (currentItemOpt.isPresent()) {
            itemDto.setId(itemId);

            Item currentItem = currentItemOpt.get();
            //Функционал ItemRequest будет реализован в следующем спринте, поэтому значение null
            Item item = ItemDtoMapper.dtoToItem(itemDto, user, null);

            if (itemDto.getDescription() == null) {
                item.setDescription(currentItem.getDescription());
            }

            if (itemDto.getName() == null) {
                item.setName(currentItem.getName());
            }

            if (itemDto.getAvailable() == null) {
                item.setAvailable(currentItem.getAvailable());
            }

            if (itemDto.getRequestId() == null) {
                item.setRequest(currentItem.getRequest());
            }

            Set<ConstraintViolation<Item>> violations = validator.validate(item);
            if (violations.size() > 0) {
                throw new ConstraintViolationException(violations);
            }

            item = itemRepository.save(item);

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
    //Информация по датам аренды только для пользователя, который еще не арендовал вещ.
    public ItemDtoWithBookings getItemDtoWithBookingsById(long itemId, long userId) {
        Booking lastBooking;
        Booking nextBooking;
        List<Booking> bookingsSorted;
        List<Booking> bookingsAfterNowSorted;

        Optional<Item> itemOpt = itemRepository.findById(itemId);

        if (itemOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Вещь id=%d не найдена"
                    , itemId)
                    , Map.of("Object", "Item"
                    , "Id", String.valueOf(itemId)
                    , "Description", "Item not found"));
        }

        log.trace("Item id={} отправлен: {}", itemId, itemOpt.get());

        if (!bookingRepository.findAllByItem_idAndBooker_id(itemId, userId).isEmpty()) {
            lastBooking = null;
            nextBooking = null;
        } else {
            bookingsSorted = bookingRepository
                    .findAllByItemAndStartDateBeforeNowSorted(itemOpt.get().getId(), LocalDateTime.now());
            lastBooking = bookingsSorted.size() == 0 ? null : bookingsSorted.get(0);

            bookingsAfterNowSorted = bookingRepository
                    .findAllByItemAndStartDateAfterNowSorted(itemOpt.get().getId(), LocalDateTime.now());
            nextBooking = bookingsAfterNowSorted.size() == 0 ? null : bookingsAfterNowSorted.get(0);
        }

        return ItemDtoMapper.itemToDtoWithBookings(itemOpt.get()
                , lastBooking, nextBooking);
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    public Item getItemById(long itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);

        if (itemOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Вещь id=%d не найдена"
                    , itemId)
                    , Map.of("Object", "Item"
                    , "Id", String.valueOf(itemId)
                    , "Description", "Item not found"));
        }

        log.trace("Item id={} отправлен: {}", itemId, itemOpt.get());

        return itemOpt.get();
    }

    //Просмотр владельцем списка всех его вещей.
    public List<ItemDtoWithBookings> getAllUserItems(long userId) {
        User user = userService.getUserById(userId);
        List<Item> userItems = itemRepository.findAllByOwnerOrderById(user);

        if (userItems.isEmpty()) {
            throw new ItemNotFoundException(String.format("Вещи у пользователя id=%d не найдены"
                    , userId)
                    , Map.of("Object", "Item"
                    , "UserId", String.valueOf(userId)
                    , "Description", "Items not found"));
        }

        List<ItemDtoWithBookings> itemDtoWithBookings = userItems.stream()
                .map(item -> this.getItemDtoWithBookingsById(item.getId(), userId))
                .collect(Collectors.toList());

        log.trace("Все {} Items пользователя id={} отправлены.", userItems.size(), userId);

        return itemDtoWithBookings;
    }

    //Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст,
    //и система ищет вещи, содержащие этот текст в названии или описании.
    //Поиск возвращает только доступные для аренды вещи.
    public List<Item> findItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> itemsFound = itemRepository.findAllByNameOrDescriptionContainsIgnoreCase(text);

        log.trace("Найдено {} Items содержащих <{}>.", itemsFound.size(), text);

        return itemsFound;
    }
}
