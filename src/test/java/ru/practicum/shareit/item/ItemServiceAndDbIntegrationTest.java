package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceAndDbIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;
    private final EntityManager manager;

    private Item item;
    private ItemDto itemDto;
    private User userSecond;
    private Booking bookingFirst;
    private Booking bookingSecond;
    private Comment comment;

    @BeforeEach
    void initialize() {
        User userFirst = new User(10L, "name 10", "email@10.ru");
        userSecond = new User(20L, "name 20", "email@20.ru");
        userFirst = userService.addUser(userFirst);
        userSecond = userService.addUser(userSecond);

        ItemRequest itemRequest = new ItemRequest(5L, "description 5", userSecond, LocalDateTime.now().minusHours(1));
        ItemRequestDto itemRequestDto = itemRequestService.addRequest(itemRequest.getRequestor().getId()
                , ItemRequestDtoMapper.itemRequestToDto(itemRequest));
        itemRequest.setId(itemRequestDto.getId());

        item = new Item(15L, "name 15", "description 15", true, userFirst, itemRequest);
        itemDto = ItemDtoMapper.ItemToDto(item);
        item = itemService.addItem(itemDto, item.getOwner().getId());
        itemDto.setId(item.getId());

        bookingFirst = new Booking(2L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)
                , item, userSecond, Status.APPROVED);
        bookingSecond = new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
                , item, userSecond, Status.APPROVED);
        BookingDtoResponse bookingDtoResponse = bookingService.bookItem(
                new BookingDtoRequest(bookingFirst.getStartDate(), bookingFirst.getEndDate()
                        , bookingFirst.getItem().getId()), bookingFirst.getBooker().getId());
        bookingFirst.setId(bookingDtoResponse.getId());
        bookingDtoResponse = bookingService.bookItem(
                new BookingDtoRequest(bookingSecond.getStartDate(), bookingSecond.getEndDate()
                        , bookingSecond.getItem().getId()), bookingSecond.getBooker().getId());
        bookingSecond.setId(bookingDtoResponse.getId());

        bookingService.approveBooking(item.getOwner().getId(), bookingFirst.getId(), true);
        bookingService.approveBooking(item.getOwner().getId(), bookingSecond.getId(), true);

        comment = new Comment(3L, "text 3", item, userSecond, LocalDateTime.now());
        comment = itemService.addCommentToItem(comment.getAuthor().getId(), item.getId()
                , CommentDtoMapper.CommentToDto(comment));
    }

    @Test
    @Transactional
    void addItemTest() {
        item = itemService.addItem(itemDto, item.getOwner().getId());

        Item itemTest = manager.createQuery("SELECT i FROM Item i " +
                "WHERE i.id = :id", Item.class).setParameter("id", item.getId()).getSingleResult();

        assertThat(item).isEqualTo(itemTest);
    }

    @Test
    @Transactional
    void updateItemTest() {
        itemDto.setName("new name");
        item = itemService.addItem(itemDto, item.getOwner().getId());

        Item itemTest = manager.createQuery("SELECT i FROM Item i " +
                "WHERE i.id = :id", Item.class).setParameter("id", item.getId()).getSingleResult();

        assertThat(item).isEqualTo(itemTest);
    }

    @Test
    @Transactional
    void getItemDtoWithBookingsByIdTest() {
        ItemDtoWithBookings itemDtoWithBookings = itemService.getItemDtoWithBookingsById(item.getId()
                , userSecond.getId());

        Item itemTest = manager.createQuery("SELECT i FROM Item i " +
                "WHERE i.id = :id", Item.class).setParameter("id", item.getId()).getSingleResult();

        bookingFirst = manager.createQuery("SELECT b FROM Booking b " +
                        "WHERE b.item.id = ?1 " +
                        "AND b.startDate < ?2 " +
                        "ORDER BY b.startDate DESC", Booking.class).setParameter(1, item.getId())
                .setParameter(2, LocalDateTime.now()).getSingleResult();

        bookingSecond = manager.createQuery("SELECT b FROM Booking b " +
                        "WHERE b.item.id = ?1 " +
                        "AND b.startDate > ?2 " +
                        "ORDER BY b.startDate ASC", Booking.class).setParameter(1, item.getId())
                .setParameter(2, LocalDateTime.now()).getSingleResult();

        List<Comment> comments = manager.createQuery("SELECT c FROM Comment c " +
                "WHERE c.item.id = ?1", Comment.class).setParameter(1, item.getId()).getResultList();

        ItemDtoWithBookings itemDtoWithBookingsTest = ItemDtoMapper.itemToDtoWithBookings(itemTest
                , bookingFirst, bookingSecond, comments);

        assertThat(itemDtoWithBookings).isEqualTo(itemDtoWithBookingsTest);
    }

    @Test
    @Transactional
    void getItemByIdTest() {
        Item itemTest = itemService.getItemById(item.getId());
        assertThat(item).isEqualTo(itemTest);
    }

    @Test
    @Transactional
    void getAllUserItemsTest() {
        List<ItemDtoWithBookings> items = itemService.getAllUserItems(item.getOwner().getId(), 0, 10);
        List<ItemDtoWithBookings> itemsTest = List.of(ItemDtoMapper.itemToDtoWithBookings(item
                , bookingFirst, bookingSecond, List.of(comment)));
        assertThat(items).isEqualTo(itemsTest);
    }

    @Test
    @Transactional
    void findItemsTest() {
        List<Item> items = itemService.findItems("15", 0, 10);
        assertThat(items).isEqualTo(List.of(item));
    }

    @Test
    @Transactional
    void addCommentToItemTest() {
        CommentDto commentDtoNew = new CommentDto();
        commentDtoNew.setText("new comment");
        Comment commentTest = itemService.addCommentToItem(userSecond.getId(), item.getId(), commentDtoNew);

        comment = manager.createQuery("SELECT c FROM Comment c " +
                "WHERE c.id = ?1", Comment.class).setParameter(1, commentTest.getId()).getSingleResult();
    }
}