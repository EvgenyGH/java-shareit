package ru.practicum.shareitgateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.request.client.ItemRequestClient;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.Optional;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient client;

    //добавить новый запрос вещи.
    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.trace("Gateway: addRequest: userIt={}, itemRequestDto={}", userId, itemRequestDto);
        return client.addRequest(userId, itemRequestDto);
    }

    //Получить список своих запросов вместе с данными об ответах на них.
    //Запросы отсортированы от более новых к более старым.
    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.trace("Gateway: getUserRequests: userIt={}", userId);
        return client.getUserRequests(userId);
    }

    //Получить список запросов, созданных другими пользователями.
    //Запросы сортируются от более новых к более старым.
    //Результаты возвращаются постранично.
    //from — индекс первого элемента, size — количество элементов для отображения.
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                 @RequestParam Optional<Integer> from,
                                                 @RequestParam Optional<Integer> size) {
        log.trace("Gateway: getAllRequests: ownerId={}, from={}, size={}", ownerId, from, size);
        return client.getAllRequests(ownerId, from, size);
    }

    //Получить данные об одном конкретном запросе вместе с данными об ответах.
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.trace("Gateway: getRequestById: userIt={}, requestId={}", userId, requestId);
        return client.getRequestById(userId, requestId);
    }
}
