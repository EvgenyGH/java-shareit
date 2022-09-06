package ru.practicum.shareitgateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.booking.client.BookingClient;
import ru.practicum.shareitserver.booking.dto.BookingDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class BookingController {
    private final BookingClient client;
    //Добавление нового запроса на бронирование.
    //Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
    //После создания запрос находится в статусе WAITING — «ожидает подтверждения».
    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestBody @Valid BookingDtoRequest bookingDtoRequest,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.trace("Gateway: bookItem: userId={}, BookingDtoRequest={}", userId, bookingDtoRequest);
        return client.bookItem(userId, bookingDtoRequest);
    }

    //Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    // Затем статус бронирования становится либо APPROVED, либо REJECTED
    //параметр approved может принимать значения true или false.
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam boolean approved, @PathVariable long bookingId) {
        log.trace("Gateway: approveBooking: userId={}, bookingId={}, approved={}", userId, bookingId, approved);
        return client.approveBooking(userId, approved, bookingId);
    }

    //Получение данных о конкретном бронировании (включая его статус). Может быть выполнено либо автором бронирования,
    //либо владельцем вещи, к которой относится бронирование.
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId) {
        log.trace("Gateway: getBooking: userId={}, bookingId={}", userId, bookingId);
        return client.getBooking(userId, bookingId);
    }

    //Получение списка всех бронирований текущего пользователя.
    //Параметр state необязательный и по умолчанию равен ALL.
    //Также он может принимать значения CURRENT, PAST, FUTURE, WAITING, REJECTED.
    //Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
    @GetMapping
    public ResponseEntity<Object> getUserBookingByStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(required = false,
                                                                 defaultValue = "ALL") String state,
                                                         @RequestParam(required = false,
                                                                 defaultValue = "0") @Min(0) int from,
                                                         @RequestParam(required = false,
                                                                 defaultValue = "10") @Min(1) int size) {
        log.trace("Gateway: getUserBookingByStatus: userId={}, state={}, from={}, size={}",
                userId, state, from, size);
        return client.getUserBookingByStatus(userId, state, from, size);
    }

    //Получение списка бронирований для всех вещей текущего пользователя.
    //Этот запрос имеет смысл для владельца хотя бы одной вещи.
    //Работа параметра state аналогична его работе в предыдущем сценарии.
    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(required = false,
                                                             defaultValue = "ALL") String state,
                                                     @RequestParam(required = false,
                                                             defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(required = false,
                                                             defaultValue = "10") @Min(1) int size) {
        log.trace("Gateway: getUserBookingByStatus: userId={}, state={}, from={}, size={}",
                userId, state, from, size);
        return client.getAllUserBookings(userId, state, from, size);
    }
}
