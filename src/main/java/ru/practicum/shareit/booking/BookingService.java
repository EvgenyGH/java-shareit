package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.exception.ItemNotAvailableException;
import ru.practicum.shareit.booking.exception.StartAfterEndExeption;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.UserService;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    public BookingDtoResponse bookItem(BookingDtoRequest bookingDtoRequest, long userId) {
        if (bookingDtoRequest.getStart().after(bookingDtoRequest.getEnd())) {
            throw new StartAfterEndExeption("Начало аренды после ее окончания"
                    , Map.of("Object", "Booking"
                    , "Start", bookingDtoRequest.getStart().toString()
                    , "End", bookingDtoRequest.getEnd().toString()
                    , "Description", "Start after the end"));
        }

        Item item = itemService.getItemById(bookingDtoRequest.getItemId());

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Вещь id=%d недоступна.", item.getId())
                    , Map.of("Object", "Item"
                    , "id", String.valueOf(item.getId())
                    , "Description", "Item not available"));
        }

        Booking booking = BookingDtoMapper.DtoRequestToBooking(bookingDtoRequest
                , item, userService.getUserById(userId));

        booking.setStatus(Status.WAITING);

        booking = bookingRepository.save(booking);

        item.setAvailable(false);
        itemService.updateItem(ItemDtoMapper.ItemToDto(item), item.getOwner().getId(), item.getId());

        log.trace("Добавлен запрос на аренду вещи id={} пользователем id={} c {} по {}."
                , booking.getItem().getId(), booking.getBooker().getId()
                , booking.getStartDate().toString(), booking.getEndDate().toString());

        return BookingDtoMapper.bookingToDto(booking);
    }
}
