package ru.practicum.shareit.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
}
