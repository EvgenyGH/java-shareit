package ru.practicum.shareitserver.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareitserver.booking.Booking;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.booking.exception.ItemNotRentedException;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.item.comment.Comment;
import ru.practicum.shareitserver.item.comment.dto.CommentDto;
import ru.practicum.shareitserver.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareitserver.item.comment.repository.CommentRepository;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.dto.ItemDtoMapper;
import ru.practicum.shareitserver.item.dto.ItemDtoWithBookings;
import ru.practicum.shareitserver.item.exception.ItemNotFoundException;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.item.service.ItemServiceImpl;
import ru.practicum.shareitserver.request.ItemRequest;
import ru.practicum.shareitserver.request.service.ItemRequestService;
import ru.practicum.shareitserver.user.User;
import ru.practicum.shareitserver.user.service.UserService;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplUnitTest {
    @InjectMocks
    @Spy
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestService itemRequestService;

    private Item item;
    private User user;

    @BeforeEach
    void initialize() {
        user = new User(10L, "user name 10", "email@10.com");
        item = new Item(15L, "item name 15", "description 15", true, user, null);
    }

    @Test
    void addItemWithRequestTest() {
        ItemRequest itemRequest = new ItemRequest(20L, "description 20", item.getOwner(),
                LocalDateTime.now());
        item.setRequest(itemRequest);
        ItemDto itemDto = ItemDtoMapper.itemToDto(item);

        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        when(itemRequestService.getItemRequestById(itemRequest.getId())).thenReturn(itemRequest);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item itemTest = itemService.addItem(itemDto, item.getOwner().getId());

        assertThat(itemTest).isEqualTo(item);
    }

    @Test
    void addItemWithoutRequestTest() {
        ItemDto itemDto = ItemDtoMapper.itemToDto(item);

        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item itemTest = itemService.addItem(itemDto, item.getOwner().getId());

        assertThat(itemTest).isEqualTo(item);
    }

    @Test
    void updateItemTest() {
        ItemDto itemDto = ItemDtoMapper.itemToDto(item);

        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        when(itemRepository.findByIdAndOwner(item.getId(), item.getOwner())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(item)).thenReturn(item);

        Item itemTest = itemService.updateItem(itemDto, item.getOwner().getId(), item.getId());

        assertThat(itemTest).isEqualTo(item);
    }

    @Test
    void updateItemRequestIdNotNullTest() {
        item.setRequest(new ItemRequest(300L, "description 300", user, LocalDateTime.now()));
        ItemDto itemDto = ItemDtoMapper.itemToDto(item);

        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        when(itemRepository.findByIdAndOwner(item.getId(), item.getOwner())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(item)).thenReturn(item);

        Item itemTest = itemService.updateItem(itemDto, item.getOwner().getId(), item.getId());

        assertThat(itemTest).isEqualTo(item);
    }

    @Test
    void updateItemWithNullValuesTest() {
        Item itemNulls = new Item();
        itemNulls.setAvailable(null);
        itemNulls.setDescription(null);
        itemNulls.setRequest(null);
        itemNulls.setOwner(null);
        itemNulls.setId(null);
        itemNulls.setName(null);

        ItemDto itemDto = ItemDtoMapper.itemToDto(itemNulls);

        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        when(itemRepository.findByIdAndOwner(item.getId(), item.getOwner())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(item)).thenReturn(item);

        Item itemTest = itemService.updateItem(itemDto, item.getOwner().getId(), item.getId());

        assertThat(itemTest).isEqualTo(item);
    }

    @Test
    void updateItemConstraintViolationExceptionTest() {
        item.setName("");
        ItemDto itemDto = ItemDtoMapper.itemToDto(item);

        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        when(itemRepository.findByIdAndOwner(item.getId(), item.getOwner())).thenReturn(Optional.ofNullable(item));

        assertThatThrownBy(() -> itemService.updateItem(itemDto, item.getOwner().getId(), item.getId()))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void updateItemItemNotFoundExceptionTest() {
        ItemDto itemDto = ItemDtoMapper.itemToDto(item);

        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        when(itemRepository.findByIdAndOwner(item.getId(), item.getOwner())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(itemDto, item.getOwner().getId(), item.getId()))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void getItemDtoWithBookingsByIdTest() {
        Booking bookingLast = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5),
                item, item.getOwner(), Status.APPROVED);
        Booking bookingNext = new Booking(2L, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(11),
                item, item.getOwner(), Status.APPROVED);
        List<Comment> comments = List.of(new Comment(1L, "text", item, item.getOwner(),
                LocalDateTime.now().minusDays(20)));
        ItemDtoWithBookings itemDtoWithBookings = ItemDtoMapper.itemToDtoWithBookings(item,
                bookingLast, bookingNext, comments);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.getBookingByItemBooker(item.getId(), item.getOwner().getId()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.getLastItemBookingOrdered(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(List.of(bookingLast));
        when(bookingRepository.getNextItemBookingOrdered(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(List.of(bookingNext));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(comments);

        assertThat(itemService.getItemDtoWithBookingsById(item.getId(), item.getOwner().getId()))
                .isEqualTo(itemDtoWithBookings);
    }

    @Test
    void getItemDtoWithBookingsByIdWithNullBookingsTest() {
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5),
                item, item.getOwner(), Status.APPROVED);
        List<Comment> comments = List.of(new Comment(1L, "text", item, item.getOwner(),
                LocalDateTime.now().minusDays(20)));
        ItemDtoWithBookings itemDtoWithBookings = ItemDtoMapper.itemToDtoWithBookings(item,
                null, null, comments);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.getBookingByItemBooker(item.getId(), item.getOwner().getId()))
                .thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(comments);

        assertThat(itemService.getItemDtoWithBookingsById(item.getId(), item.getOwner().getId()))
                .isEqualTo(itemDtoWithBookings);

    }

    @Test
    void getItemDtoWithBookingsByIdItemNotFoundExceptionTest() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemDtoWithBookingsById(item.getId(), item.getOwner().getId()))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void getItemByIdTest() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        assertThat(itemService.getItemById(item.getId())).isEqualTo(item);
    }

    @Test
    void getItemByIdItemNotFoundExceptionTest() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.getItemById(item.getId())).isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void getAllUserItemsTest() {
        ItemDtoWithBookings itemDtoWithBookings = ItemDtoMapper.itemToDtoWithBookings(item,
                null, null, Collections.emptyList());
        List<ItemDtoWithBookings> bookings = List.of(itemDtoWithBookings);

        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        when(itemRepository.findAllByOwnerOrderById(item.getOwner(), PageRequest.of(0, 10)))
                .thenReturn(List.of(item));
        doReturn(itemDtoWithBookings)
                .when(itemService).getItemDtoWithBookingsById(item.getId(), item.getOwner().getId());

        assertThat(itemService.getAllUserItems(item.getOwner().getId(), 0, 10)).isEqualTo(bookings);
    }

    @Test
    void getAllUserItemsItemNotFoundExceptionTest() {
        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        when(itemRepository.findAllByOwnerOrderById(item.getOwner(), PageRequest.of(0, 10)))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> itemService.getAllUserItems(item.getOwner().getId(), 0, 10))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void findItemsTest() {
        when(itemRepository.findAllByNameOrDescriptionIgnoreCase(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(item));
        assertThat(itemService.findItems("text", 0, 10)).isEqualTo(List.of(item));
    }

    @Test
    void findItemsWithNullTextTest() {
        assertThat(itemService.findItems(null, 0, 10)).isEqualTo(Collections.emptyList());
    }

    @Test
    void findItemsWithBlankTextTest() {
        assertThat(itemService.findItems("", 0, 10)).isEqualTo(Collections.emptyList());
    }

    @Test
    void addCommentToItemTest() {
        Comment comment = new Comment(1L, "text", item, item.getOwner(), LocalDateTime.now());
        CommentDto commentDto = CommentDtoMapper.commentToDto(comment);
        User booker = new User(15L, "user name 15", "email@15.com");
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(12),
                item, booker, Status.APPROVED);

        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        doReturn(item).when(itemService).getItemById(item.getId());
        when(bookingRepository.getFinishedBookingByBookerItemStatus(anyLong(), anyLong(),
                any(Status.class), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(commentRepository.save(comment)).thenReturn(comment);

        assertThat(itemService.addCommentToItem(item.getOwner().getId(), item.getId(), commentDto))
                .isEqualTo(comment);
    }

    @Test
    void addCommentToItemItemNotRentedExceptionTest() {
        Comment comment = new Comment(1L, "text", item, item.getOwner(), LocalDateTime.now());
        CommentDto commentDto = CommentDtoMapper.commentToDto(comment);

        when(userService.getUserById(item.getOwner().getId())).thenReturn(item.getOwner());
        doReturn(item).when(itemService).getItemById(item.getId());
        when(bookingRepository.getFinishedBookingByBookerItemStatus(anyLong(), anyLong(),
                any(Status.class), any(LocalDateTime.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addCommentToItem(item.getOwner().getId(), item.getId(), commentDto))
                .isInstanceOf(ItemNotRentedException.class);
    }
}
