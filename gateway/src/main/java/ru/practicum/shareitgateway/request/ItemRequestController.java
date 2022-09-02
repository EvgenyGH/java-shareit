package ru.practicum.shareitgateway.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.dto.ItemRequestDtoWithResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    //добавить новый запрос вещи.
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return null; //itemRequestService.addRequest(userId, itemRequestDto);
    }

    //Получить список своих запросов вместе с данными об ответах на них.
    //Запросы отсортированы от более новых к более старым.
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoWithResponse> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return null; //itemRequestService.getUserRequests(userId);
    }

    //Получить список запросов, созданных другими пользователями.
    //Запросы сортируются от более новых к более старым.
    //Результаты возвращаются постранично.
    //from — индекс первого элемента, size — количество элементов для отображения.
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoWithResponse> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                           @RequestParam Optional<Integer> from,
                                                           @RequestParam Optional<Integer> size) {
        return null; //itemRequestService.getAllRequests(from, size, ownerId);
    }

    //Получить данные об одном конкретном запросе вместе с данными об ответах.
    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDtoWithResponse getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long requestId) {
        return null; //itemRequestService.getRequestById(userId, requestId);
    }
}
