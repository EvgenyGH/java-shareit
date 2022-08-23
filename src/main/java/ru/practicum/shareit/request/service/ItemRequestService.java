package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponse;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    //добавить новый запрос вещи.
    ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto);

    //Получить список своих запросов вместе с данными об ответах на них.
    //Запросы отсортированы от более новых к более старым.
    List<ItemRequestDtoWithResponse> getUserRequests(Long userId);

    //Получить список запросов, созданных другими пользователями.
    //Запросы сортируются от более новых к более старым.
    //Результаты возвращаются постранично.
    //from — индекс первого элемента, size — количество элементов для отображения.
    List<ItemRequestDtoWithResponse> getAllRequests(Optional<Integer> fromOpt, Optional<Integer> sizeOpt,
                                                    Long ownerId);

    //Получить данные об одном конкретном запросе вместе с данными об ответах по id.
    ItemRequestDtoWithResponse getRequestById(Long userId, Long requestId);

    //Получить запрос по id.
    ItemRequest getItemRequestById(Long requestId);
}
