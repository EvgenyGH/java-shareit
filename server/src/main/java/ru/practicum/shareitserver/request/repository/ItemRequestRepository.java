package ru.practicum.shareitserver.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareitserver.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT ir FROM ItemRequest ir " +
            "WHERE ir.requestor.id = ?1 " +
            "ORDER BY ir.createdDateTime DESC")
    List<ItemRequest> findAllByRequestorId(Long requestorId);

    @Query("SELECT ir FROM ItemRequest ir " +
            "WHERE ir.requestor.id <> ?1 " +
            "ORDER BY ir.createdDateTime DESC")
    List<ItemRequest> findAllNotOwner(Long ownerId, Pageable pageable);
}
