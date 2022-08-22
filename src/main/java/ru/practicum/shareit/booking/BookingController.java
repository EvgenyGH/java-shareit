package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    //Добавление нового запроса на бронирование.
    //Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
    //После создания запрос находится в статусе WAITING — «ожидает подтверждения».
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoResponse bookItem(@RequestBody @Valid BookingDtoRequest bookingDtoRequest
            , @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.bookItem(bookingDtoRequest, userId);
    }

    //Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    // Затем статус бронирования становится либо APPROVED, либо REJECTED
    //параметр approved может принимать значения true или false.
    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoResponse approveBooking(@/**/RequestHeader("X-Sharer-User-Id") long userId
            , @RequestParam boolean approved, @PathVariable long bookingId) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    //Получение данных о конкретном бронировании (включая его статус). Может быть выполнено либо автором бронирования,
    //либо владельцем вещи, к которой относится бронирование.
    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoResponse getBooking(@RequestHeader("X-Sharer-User-Id") long userId
            , @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    //Получение списка всех бронирований текущего пользователя.
    //Параметр state необязательный и по умолчанию равен ALL.
    //Также он может принимать значения CURRENT, PAST, FUTURE, WAITING, REJECTED.
    //Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDtoResponse> getUserBookingByStatus(@RequestHeader("X-Sharer-User-Id") long userId
            , @RequestParam(required = false, defaultValue = "ALL") String state
            , @RequestParam(required = false, defaultValue = "0") @Min(0) int from
            , @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        return bookingService.getUserBookingByStatus(userId, RequestStatus.valueOf(state), from, size);
    }

    //Получение списка бронирований для всех вещей текущего пользователя.
    //Этот запрос имеет смысл для владельца хотя бы одной вещи.
    //Работа параметра state аналогична его работе в предыдущем сценарии.
    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDtoResponse> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") long userId
            , @RequestParam(required = false, defaultValue = "ALL") String state
            , @RequestParam(required = false, defaultValue = "0") @Min(0) int from
            , @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        return bookingService.getAllUserBookings(userId, RequestStatus.valueOf(state), from, size);
    }
}
