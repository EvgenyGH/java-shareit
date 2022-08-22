package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplUnitTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingRepository bookingRepository;

    private User userFirst;
    private User userSecond;
    private Item item;
    private Booking bookingFirst;
    private Booking bookingSecond;

    @BeforeEach
    void initialization() {
        userFirst = new User(25L, "name 25", "email@25.com");
        userSecond = new User(35L, "name 35", "email@35.com");
        item = new Item(105L, "name 105", "description 105"
                , true, userFirst, null);
        bookingFirst = new Booking(115L, LocalDateTime.now().minusDays(15)
                , LocalDateTime.now().minusDays(12), item, userSecond, Status.APPROVED);
        bookingSecond = new Booking(125L, LocalDateTime.now().plusDays(15)
                , LocalDateTime.now().plusDays(20), item, userSecond, Status.APPROVED);
    }

    @Test
    void bookItemTest() {
        BookingDtoResponse dtoResponse = BookingDtoMapper.bookingToDto(bookingFirst);
        BookingDtoRequest dtoRequest = new BookingDtoRequest(bookingFirst.getStartDate()
                , bookingFirst.getEndDate(), bookingFirst.getItem().getId());

        when(itemService.getItemById(item.getId())).thenReturn(item);
        when(userService.getUserById(userSecond.getId())).thenReturn(userSecond);
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingFirst);

        BookingDtoResponse dtoResponseTest = bookingService
                .bookItem(dtoRequest, bookingFirst.getBooker().getId());

        assertThat(dtoResponseTest).isEqualTo(dtoResponse);
    }

    @Test
    void bookItemStartAfterEndExeptionTest() {
        bookingFirst.setEndDate(bookingFirst.getStartDate().minusDays(1));
        BookingDtoRequest dtoRequest = new BookingDtoRequest(bookingFirst.getStartDate()
                , bookingFirst.getEndDate(), bookingFirst.getItem().getId());

        assertThatThrownBy(() -> bookingService.bookItem(dtoRequest, bookingFirst.getBooker().getId()))
                .isInstanceOf(StartAfterEndException.class);
    }

    @Test
    void bookItemItemNotAvailableExceptionTest() {
        item.setAvailable(false);
        BookingDtoRequest dtoRequest = new BookingDtoRequest(bookingFirst.getStartDate()
                , bookingFirst.getEndDate(), bookingFirst.getItem().getId());

        when(itemService.getItemById(item.getId())).thenReturn(item);

        assertThatThrownBy(() -> bookingService.bookItem(dtoRequest, bookingFirst.getBooker().getId()))
                .isInstanceOf(ItemNotAvailableException.class);
    }

    @Test
    void bookItemBookingExceptionTest() {
        item.setOwner(userSecond);
        BookingDtoRequest dtoRequest = new BookingDtoRequest(bookingFirst.getStartDate()
                , bookingFirst.getEndDate(), bookingFirst.getItem().getId());

        when(itemService.getItemById(item.getId())).thenReturn(item);

        assertThatThrownBy(() -> bookingService.bookItem(dtoRequest, bookingFirst.getBooker().getId()))
                .isInstanceOf(BookingException.class);
    }

    @Test
    void approveBookingApproveStatusTest() {
        bookingFirst.setStatus(Status.WAITING);
        when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));
        when(bookingRepository.save(bookingFirst))
                .thenAnswer((booking) -> {
                    bookingFirst.setStatus(Status.APPROVED);
                    return bookingFirst;
                });

        BookingDtoResponse dtoResponseTest = bookingService.approveBooking(userFirst.getId()
                , bookingFirst.getId(), true);

        assertThat(dtoResponseTest.getId()).isEqualTo(bookingFirst.getId().intValue());
        assertThat(dtoResponseTest.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void approveBookingRejectStatusTest() {
        bookingFirst.setStatus(Status.WAITING);
        when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));
        when(bookingRepository.save(bookingFirst))
                .thenAnswer((booking) -> {
                    bookingFirst.setStatus(Status.REJECTED);
                    return bookingFirst;
                });

        BookingDtoResponse dtoResponseTest = bookingService.approveBooking(userFirst.getId()
                , bookingFirst.getId(), true);

        assertThat(dtoResponseTest.getId()).isEqualTo(bookingFirst.getId().intValue());
        assertThat(dtoResponseTest.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void approveBookingBookingNotExistsExceptionTest() {
        when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.approveBooking(userFirst.getId()
                , bookingFirst.getId(), true)).isInstanceOf(BookingNotExistsException.class);
    }

    @Test
    void approveBookingUserNotOwnerExceptionTest() {
        when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));

        assertThatThrownBy(() -> bookingService.approveBooking(userSecond.getId()
                , bookingFirst.getId(), true)).isInstanceOf(UserNotOwnerException.class);
    }

    @Test
    void approveBookingItemNotAvailableExceptionTest() {
        bookingFirst.setStatus(Status.APPROVED);
        when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));

        assertThatThrownBy(() -> bookingService.approveBooking(userFirst.getId()
                , bookingFirst.getId(), true)).isInstanceOf(ItemNotAvailableException.class);
    }

    @Test
    void getBookingTest() {
        BookingDtoResponse dtoResponse = BookingDtoMapper.bookingToDto(bookingFirst);

        when(bookingRepository.getOwnerOrBookerBooking(bookingFirst.getId(), userSecond.getId()))
                .thenReturn(Optional.of(bookingFirst));

        BookingDtoResponse dtoResponseTest = bookingService.getBooking(userSecond.getId()
                , bookingFirst.getId());

        assertThat(dtoResponseTest).isEqualTo(dtoResponse);
    }

    @Test
    void getBookingBookingExceptionTest() {
        when(bookingRepository.getOwnerOrBookerBooking(bookingFirst.getId(), userSecond.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBooking(userSecond.getId()
                , bookingFirst.getId())).isInstanceOf(BookingException.class);
    }

    @Test
    void getUserBookingByStatusALLTest() {
        when(userService.getUserById(userSecond.getId())).thenReturn(userSecond);
        when(bookingRepository.getBookingByBookerStatusAllOrdered(userSecond.getId()
                , PageRequest.of(0, 20))).thenReturn(List.of(bookingFirst, bookingSecond));

        List<BookingDtoResponse> bookingsTest = bookingService
                .getUserBookingByStatus(userSecond.getId(), RequestStatus.ALL, 0, 20);

        assertThat(bookingsTest).isEqualTo(Stream.of(bookingFirst, bookingSecond)
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    void getUserBookingByStatusWaitingTest() {
        bookingFirst.setStatus(Status.WAITING);
        when(userService.getUserById(userSecond.getId())).thenReturn(userSecond);
        when(bookingRepository.getBookingByBookerStatusOrdered(userSecond.getId(), Status.WAITING
                , PageRequest.of(0, 20))).thenReturn(List.of(bookingFirst));

        List<BookingDtoResponse> bookingsTest = bookingService
                .getUserBookingByStatus(userSecond.getId(), RequestStatus.WAITING, 0, 20);

        assertThat(bookingsTest).isEqualTo(Stream.of(bookingFirst)
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    void getUserBookingByStatusRejectedTest() {
        bookingFirst.setStatus(Status.REJECTED);
        when(userService.getUserById(userSecond.getId())).thenReturn(userSecond);
        when(bookingRepository.getBookingByBookerStatusOrdered(userSecond.getId(), Status.REJECTED
                , PageRequest.of(0, 20))).thenReturn(List.of(bookingFirst));

        List<BookingDtoResponse> bookingsTest = bookingService
                .getUserBookingByStatus(userSecond.getId(), RequestStatus.REJECTED, 0, 20);

        assertThat(bookingsTest).isEqualTo(Stream.of(bookingFirst)
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    void getUserBookingByStatusPastTest() {
        when(userService.getUserById(userSecond.getId())).thenReturn(userSecond);
        when(bookingRepository.getBookingByBookerStatusPastOrdered(anyLong(), any(LocalDateTime.class)
                , any(PageRequest.class))).thenReturn(List.of(bookingFirst));

        List<BookingDtoResponse> bookingsTest = bookingService
                .getUserBookingByStatus(userSecond.getId(), RequestStatus.PAST, 0, 20);

        assertThat(bookingsTest).isEqualTo(Stream.of(bookingFirst)
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    void getUserBookingByStatusFutureTest() {
        when(userService.getUserById(userSecond.getId())).thenReturn(userSecond);
        when(bookingRepository.getBookingByBookerStatusFutureOrdered(anyLong(), any(LocalDateTime.class)
                , any(PageRequest.class))).thenReturn(List.of(bookingSecond));

        List<BookingDtoResponse> bookingsTest = bookingService
                .getUserBookingByStatus(userSecond.getId(), RequestStatus.FUTURE, 0, 20);

        assertThat(bookingsTest).isEqualTo(Stream.of(bookingSecond)
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    void getUserBookingByStatusCurrentTest() {
        when(userService.getUserById(userSecond.getId())).thenReturn(userSecond);
        when(bookingRepository.getBookingByBookerStatusCurrentOrdered(anyLong(), any(LocalDateTime.class)
                , any(PageRequest.class))).thenReturn(Collections.emptyList());

        List<BookingDtoResponse> bookingsTest = bookingService
                .getUserBookingByStatus(userSecond.getId(), RequestStatus.CURRENT, 0, 20);

        assertThat(bookingsTest.size()).isEqualTo(0);
    }

    @Test
    void getAllUserBookingsStatusALLTest() {
        when(userService.getUserById(userFirst.getId())).thenReturn(userFirst);
        when(bookingRepository.getBookingByOwnerStatusAllOrdered(userFirst.getId()
                , PageRequest.of(0, 20))).thenReturn(List.of(bookingFirst, bookingSecond));

        List<BookingDtoResponse> bookingsTest = bookingService
                .getAllUserBookings(userFirst.getId(), RequestStatus.ALL, 0, 20);

        assertThat(bookingsTest).isEqualTo(Stream.of(bookingFirst, bookingSecond)
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    void getAllUserBookingsStatusWaitingTest() {
        bookingFirst.setStatus(Status.WAITING);
        when(userService.getUserById(userFirst.getId())).thenReturn(userFirst);
        when(bookingRepository.getBookingByOwnerStatusOrdered(userFirst.getId(), Status.WAITING
                , PageRequest.of(0, 20))).thenReturn(List.of(bookingFirst));

        List<BookingDtoResponse> bookingsTest = bookingService
                .getAllUserBookings(userFirst.getId(), RequestStatus.WAITING, 0, 20);

        assertThat(bookingsTest).isEqualTo(Stream.of(bookingFirst)
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    void getAllUserBookingsStatusRejectedTest() {
        bookingFirst.setStatus(Status.REJECTED);
        when(userService.getUserById(userFirst.getId())).thenReturn(userFirst);
        when(bookingRepository.getBookingByOwnerStatusOrdered(userFirst.getId(), Status.REJECTED
                , PageRequest.of(0, 20))).thenReturn(List.of(bookingFirst));

        List<BookingDtoResponse> bookingsTest = bookingService
                .getAllUserBookings(userFirst.getId(), RequestStatus.REJECTED, 0, 20);

        assertThat(bookingsTest).isEqualTo(Stream.of(bookingFirst)
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    void getAllUserBookingsStatusPastTest() {
        when(userService.getUserById(userFirst.getId())).thenReturn(userFirst);
        when(bookingRepository.getBookingByOwnerStatusPastOrdered(anyLong(), any(LocalDateTime.class)
                , any(PageRequest.class))).thenReturn(List.of(bookingFirst));

        List<BookingDtoResponse> bookingsTest = bookingService
                .getAllUserBookings(userFirst.getId(), RequestStatus.PAST, 0, 20);

        assertThat(bookingsTest).isEqualTo(Stream.of(bookingFirst)
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    void getAllUserBookingsStatusFutureTest() {
        when(userService.getUserById(userFirst.getId())).thenReturn(userFirst);
        when(bookingRepository.getBookingByOwnerStatusFutureOrdered(anyLong(), any(LocalDateTime.class)
                , any(PageRequest.class))).thenReturn(List.of(bookingSecond));

        List<BookingDtoResponse> bookingsTest = bookingService
                .getAllUserBookings(userFirst.getId(), RequestStatus.FUTURE, 0, 20);

        assertThat(bookingsTest).isEqualTo(Stream.of(bookingSecond)
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    void getAllUserBookingsStatusCurrentTest() {
        when(userService.getUserById(userFirst.getId())).thenReturn(userFirst);
        when(bookingRepository.getBookingByOwnerStatusCurrentOrdered(anyLong(), any(LocalDateTime.class)
                , any(PageRequest.class))).thenReturn(Collections.emptyList());

        List<BookingDtoResponse> bookingsTest = bookingService
                .getAllUserBookings(userFirst.getId(), RequestStatus.CURRENT, 0, 20);

        assertThat(bookingsTest.size()).isEqualTo(0);
    }
}