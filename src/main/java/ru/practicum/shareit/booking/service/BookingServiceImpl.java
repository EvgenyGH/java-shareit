package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.RequestStatus;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDtoResponse bookItem(BookingDtoRequest bookingDtoRequest, long userId) {
        if (bookingDtoRequest.getStart().isAfter(bookingDtoRequest.getEnd())) {
            throw new StartAfterEndException("Начало аренды после ее окончания",
                    Map.of("Object", "Booking",
                            "Start", bookingDtoRequest.getStart().toString(),
                            "End", bookingDtoRequest.getEnd().toString(),
                            "Description", "Start after the end"));
        }

        Item item = itemService.getItemById(bookingDtoRequest.getItemId());

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Вещь id=%d недоступна.", item.getId()),
                    Map.of("Object", "Item",
                            "id", String.valueOf(item.getId()),
                            "Description", "Item not available"));
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new BookingException(String.format(
                    "Пользователь id=%d является владельцем вещи id=%d.",
                    userId, item.getId()),
                    Map.of("Object", "Item",
                            "id", String.valueOf(item.getId()),
                            "Description", "User is the item owner"));
        }

        Booking booking = BookingDtoMapper.dtoRequestToBooking(bookingDtoRequest,
                item, userService.getUserById(userId));

        booking.setStatus(Status.WAITING);

        booking = bookingRepository.save(booking);

        log.trace("Добавлен запрос на аренду вещи id={} пользователем id={} c {} по {}.",
                booking.getItem().getId(), booking.getBooker().getId(),
                booking.getStartDate().toString(), booking.getEndDate().toString());

        return BookingDtoMapper.bookingToDto(booking);
    }

    @Override
    public BookingDtoResponse approveBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotExistsException(String.format(
                        "Заказа id=%d не существует", bookingId),
                        Map.of("Object", "Booking",
                                "id", String.valueOf(bookingId),
                                "Description", "Request does not exist")));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserNotOwnerException(String.format(
                    "Пользователь id=%d не владелец вещи", userId),
                    Map.of("Object", "Item",
                            "id", String.valueOf(booking.getItem().getOwner().getId()),
                            "Description", "Item not available"));
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ItemNotAvailableException("Заказ уже подтвержден",
                    Map.of("Description", "Заказ уже подтвержден"));
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);

        return BookingDtoMapper.bookingToDto(booking);
    }

    @Override
    public BookingDtoResponse getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.getOwnerOrBookerBooking(bookingId, userId)
                .orElseThrow(() -> new BookingException(
                        "Заказ по запросу не найден (или пользователь не является владельцем или заказчиком)",
                        Map.of("Description", "Booking not found (or user is not owner nor booker")));

        return BookingDtoMapper.bookingToDto(booking);
    }

    public List<BookingDtoResponse> getUserBookingByStatus(long userId, RequestStatus state, int from, int size) {
        userService.getUserById(userId);

        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.getBookingByBookerStatusAllOrdered(userId,
                        PageRequest.of(from / size, size));
                break;
            case WAITING:
                bookings = bookingRepository.getBookingByBookerStatusOrdered(userId, Status.WAITING,
                        PageRequest.of(from / size, size));
                break;
            case REJECTED:
                bookings = bookingRepository.getBookingByBookerStatusOrdered(userId, Status.REJECTED,
                        PageRequest.of(from / size, size));
                break;
            case PAST:
                bookings = bookingRepository
                        .getBookingByBookerStatusPastOrdered(userId, LocalDateTime.now(),
                                PageRequest.of(from / size, size));
                break;
            case FUTURE:
                bookings = bookingRepository
                        .getBookingByBookerStatusFutureOrdered(userId, LocalDateTime.now(),
                                PageRequest.of(from / size, size));
                break;
            case CURRENT:
                bookings = bookingRepository.getBookingByBookerStatusCurrentOrdered(userId,
                        LocalDateTime.now(), PageRequest.of(from / size, size));
                break;
            default:
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "UNSUPPORTED_STATUS");
        }

        return bookings.stream().map(BookingDtoMapper::bookingToDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getAllUserBookings(long userId, RequestStatus state, int from, int size) {
        userService.getUserById(userId);

        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.getBookingByOwnerStatusAllOrdered(userId,
                        PageRequest.of(from / size, size));
                break;
            case WAITING:
                bookings = bookingRepository.getBookingByOwnerStatusOrdered(userId, Status.WAITING,
                        PageRequest.of(from / size, size));
                break;
            case REJECTED:
                bookings = bookingRepository.getBookingByOwnerStatusOrdered(userId, Status.REJECTED,
                        PageRequest.of(from / size, size));
                break;
            case PAST:
                bookings = bookingRepository.getBookingByOwnerStatusPastOrdered(userId, LocalDateTime.now(),
                        PageRequest.of(from / size, size));
                break;
            case FUTURE:
                bookings = bookingRepository.getBookingByOwnerStatusFutureOrdered(userId, LocalDateTime.now(),
                        PageRequest.of(from / size, size));
                break;
            case CURRENT:
                bookings = bookingRepository.getBookingByOwnerStatusCurrentOrdered(userId,
                        LocalDateTime.now(), PageRequest.of(from / size, size));
                break;
            default:
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "UNSUPPORTED_STATUS");
        }

        return bookings.stream().map(BookingDtoMapper::bookingToDto).collect(Collectors.toList());
    }
}
