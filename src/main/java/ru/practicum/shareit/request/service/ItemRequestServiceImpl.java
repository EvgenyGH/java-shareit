package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponse;
import ru.practicum.shareit.request.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        itemRequestDto.setId(null);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDto,
                userService.getUserById(userId));
        itemRequest = itemRequestRepository.save(itemRequest);

        log.trace("Запрос вещи id={} добавлен: {}", itemRequest.getId(), itemRequest);

        return ItemRequestDtoMapper.itemRequestToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDtoWithResponse> getUserRequests(Long userId) {
        userService.getUserById(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);
        List<ItemRequestDtoWithResponse> itemRequestDtoWithResponse =
                itemRequests.stream().map(itemRequest -> {
                    List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
                    return ItemRequestDtoMapper.itemRequestToDtoWithResponse(itemRequest,
                            items.stream().map(ItemDtoMapper::itemToDto).collect(Collectors.toList()));
                }).collect(Collectors.toList());

        log.trace("Возвращено {} запросов пользователя id={} c ответами.", itemRequestDtoWithResponse.size(),
                userId);

        return itemRequestDtoWithResponse;
    }

    @Override
    public List<ItemRequestDtoWithResponse> getAllRequests(Optional<Integer> fromOpt, Optional<Integer> sizeOpt,
                                                           Long ownerId) {
        Integer from = fromOpt.orElse(null);
        Integer size = sizeOpt.orElse(null);

        if (from == null || size == null) {
            return Collections.emptyList();
        } else if (from < 0 || size < 1) {
            throw new ValidationException("Должно быть from >=0, size > 1");
        }

        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllNotOwner(ownerId, PageRequest.of(from / size, size));

        List<ItemRequestDtoWithResponse> itemRequestDtoWithResponseList =
                itemRequests.stream().map(itemRequest -> {
                    List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
                    return ItemRequestDtoMapper.itemRequestToDtoWithResponse(itemRequest,
                            items.stream().map(ItemDtoMapper::itemToDto).collect(Collectors.toList()));
                }).collect(Collectors.toList());

        log.trace("Возвращено {} запросов вещей с {} размер страницы {}", itemRequestDtoWithResponseList.size(),
                from, size);

        return itemRequestDtoWithResponseList;
    }

    @Override
    public ItemRequestDtoWithResponse getRequestById(Long userId, Long requestId) {
        userService.getUserById(userId);

        ItemRequest itemRequest = getItemRequestById(requestId);
        ItemRequestDtoWithResponse itemRequestDtoWithResponse =
                ItemRequestDtoMapper.itemRequestToDtoWithResponse(itemRequest,
                        itemRepository.findAllByRequestId(itemRequest.getId())
                                .stream().map(ItemDtoMapper::itemToDto).collect(Collectors.toList()));

        log.trace("Возвращен запрос вещи (DtoWithResponse) id={}: {}", requestId, itemRequestDtoWithResponse);

        return itemRequestDtoWithResponse;
    }

    @Override
    public ItemRequest getItemRequestById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new ItemRequestNotFoundException(String.format("Запрос вещи id=%d не найден", requestId),
                        Map.of("Object", "ItemRequest",
                                "Id", String.valueOf(requestId),
                                "Description", "ItemRequest not found")));

        log.trace("Возвращен запрос вещи id={}: {}", requestId, itemRequest);

        return itemRequest;
    }
}
