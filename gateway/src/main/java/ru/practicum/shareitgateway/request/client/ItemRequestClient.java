package ru.practicum.shareitgateway.request.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;

import java.util.Optional;

public interface ItemRequestClient {
    ResponseEntity<Object> addRequest(Long userId, ItemRequestDto itemRequestDto);

    ResponseEntity<Object> getUserRequests(Long userId);

    ResponseEntity<Object> getAllRequests(Long ownerId, Optional<Integer> from, Optional<Integer> size);

    ResponseEntity<Object> getRequestById(Long userId, Long requestId);
}
