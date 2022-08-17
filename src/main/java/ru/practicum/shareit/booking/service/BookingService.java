package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.RequestStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    //Добавление нового запроса на бронирование.
    //Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
    //После создания запрос находится в статусе WAITING — «ожидает подтверждения».
    BookingDtoResponse bookItem(BookingDtoRequest bookingDtoRequest, long userId);

    //Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    // Затем статус бронирования становится либо APPROVED, либо REJECTED
    //параметр approved может принимать значения true или false.
    BookingDtoResponse approveBooking(long userId, long bookingId, boolean approved);

    //Получение данных о конкретном бронировании (включая его статус). Может быть выполнено либо автором бронирования,
    //либо владельцем вещи, к которой относится бронирование.
    BookingDtoResponse getBooking(long userId, long bookingId);

    //Получение списка всех бронирований текущего пользователя.
    //Параметр state необязательный и по умолчанию равен ALL.
    //Также он может принимать значения CURRENT, PAST, FUTURE, WAITING, REJECTED.
    //Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
    List<BookingDtoResponse> getUserBookingByStatus(long userId, RequestStatus state);

    //Получение списка бронирований для всех вещей текущего пользователя.
    //Этот запрос имеет смысл для владельца хотя бы одной вещи.
    //Работа параметра state аналогична его работе в предыдущем сценарии.
    List<BookingDtoResponse> getAllUserBookings(long userId, RequestStatus state);
}
