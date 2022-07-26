package ru.practicum.shareitserver.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareitserver.booking.Booking;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.booking.exception.ItemNotRentedException;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.item.comment.Comment;
import ru.practicum.shareitserver.item.comment.dto.CommentDto;
import ru.practicum.shareitserver.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareitserver.item.comment.repository.CommentRepository;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.dto.ItemDtoMapper;
import ru.practicum.shareitserver.item.dto.ItemDtoWithBookings;
import ru.practicum.shareitserver.item.exception.ItemNotFoundException;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.request.ItemRequest;
import ru.practicum.shareitserver.request.service.ItemRequestService;
import ru.practicum.shareitserver.user.User;
import ru.practicum.shareitserver.user.service.UserService;

import javax.validation.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final Validator validator;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemRequestService itemRequestService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestService = itemRequestService;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    //Добавление новой вещи
    @Override
    public Item addItem(ItemDto itemDto, long userId) {
        User user = userService.getUserById(userId);
        itemDto.setId(null);

        ItemRequest itemRequest = itemDto.getRequestId() == null ? null
                : itemRequestService.getItemRequestById(itemDto.getRequestId());

        Item item = ItemDtoMapper.dtoToItem(itemDto, user, itemRequest);
        item = itemRepository.save(item);

        log.trace("Item id={} добавлен: {}", item.getId(), item);

        return item;
    }

    //Редактирование вещи. Редактировать вещь может только её владелец.
    @Override
    public Item updateItem(ItemDto itemDto, long userId, long itemId) {
        User user = userService.getUserById(userId);

        Item currentItem = itemRepository.findByIdAndOwner(itemId, user)
                .orElseThrow(() ->
                        new ItemNotFoundException(String.format("Вещь id=%d у пользователя id=%d не найдена",
                                itemId, userId), Map.of("Object", "Item",
                                "ItemId", String.valueOf(itemId),
                                "UserId", String.valueOf(userId),
                                "Description", "Item not found")));

        itemDto.setId(itemId);

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
        } else {
            item.setRequest(itemRequestService.getItemRequestById(itemDto.getRequestId()));
        }

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }

        item = itemRepository.save(item);

        log.trace("Item id={} обновлен: {}", itemId, item);

        return item;
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    //Информация по датам аренды только для пользователя, который еще не арендовал вещь.
    @Override
    public ItemDtoWithBookings getItemDtoWithBookingsById(long itemId, long userId) {
        Booking lastBooking;
        Booking nextBooking;
        List<Booking> bookingsSorted;
        List<Booking> bookingsAfterNowSorted;

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ItemNotFoundException(String.format("Вещь id=%d не найдена", itemId),
                                Map.of("Object", "Item",
                                        "Id", String.valueOf(itemId),
                                        "Description", "Item not found")));

        if (!bookingRepository.getBookingByItemBooker(itemId, userId).isEmpty()) {
            lastBooking = null;
            nextBooking = null;
        } else {
            bookingsSorted = bookingRepository
                    .getLastItemBookingOrdered(item.getId(), LocalDateTime.now(), PageRequest.of(0, 1));
            lastBooking = bookingsSorted.size() == 0 ? null : bookingsSorted.get(0);

            bookingsAfterNowSorted = bookingRepository
                    .getNextItemBookingOrdered(item.getId(), LocalDateTime.now(), PageRequest.of(0, 1));
            nextBooking = bookingsAfterNowSorted.size() == 0 ? null : bookingsAfterNowSorted.get(0);
        }

        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        log.trace("Item id={} отправлен: {}", itemId, item);

        return ItemDtoMapper.itemToDtoWithBookings(item,
                lastBooking, nextBooking, comments);
    }

    //Просмотр информации о вещи. Информацию о вещи может просмотреть любой пользователь.
    @Override
    public Item getItemById(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ItemNotFoundException(String.format("Вещь id=%d не найдена",
                                itemId), Map.of("Object", "Item",
                                "Id", String.valueOf(itemId),
                                "Description", "Item not found")));

        log.trace("Item id={} отправлен: {}", itemId, item);

        return item;
    }

    //Просмотр владельцем списка всех его вещей.
    @Override
    public List<ItemDtoWithBookings> getAllUserItems(long userId, int from, int size) {
        User user = userService.getUserById(userId);
        List<Item> userItems = itemRepository.findAllByOwnerOrderById(user, PageRequest.of(from / size, size));

        if (userItems.isEmpty()) {
            throw new ItemNotFoundException(String.format("Вещи у пользователя id=%d не найдены",
                    userId), Map.of("Object", "Item",
                    "UserId", String.valueOf(userId),
                    "Description", "Items not found"));
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
    @Override
    public List<Item> findItems(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> itemsFound = itemRepository.findAllByNameOrDescriptionIgnoreCase(text,
                PageRequest.of(from / size, size));

        log.trace("Найдено {} Items содержащих <{}>.", itemsFound.size(), text);

        return itemsFound;
    }

    //Добавить комментарий
    @Override
    public Comment addCommentToItem(long userId, long itemId, CommentDto commentDto) {
        User author = userService.getUserById(userId);
        Item item = this.getItemById(itemId);

        bookingRepository.getFinishedBookingByBookerItemStatus(userId, itemId,
                Status.APPROVED, LocalDateTime.now()).orElseThrow(() ->
                new ItemNotRentedException(String.format("Пользователь не арендовал вещь id=%d", userId),
                        Map.of("Object", "Item", "UserId",
                                String.valueOf(userId),
                                "ItemId", String.valueOf(itemId),
                                "Description", "Item not rented")));

        Comment comment = CommentDtoMapper.dtoToComment(commentDto, item, author);
        commentRepository.save(comment);

        return comment;
    }
}
