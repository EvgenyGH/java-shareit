package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByIdAndOwner(Long id, User owner);
    List<Item> findAllByOwnerOrderById(User owner);
    Long deleteAllByOwner(User owner);
    @Query("SELECT i FROM Item i WHERE i.available = TRUE " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(i.description) " +
            "LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> findAllByNameOrDescriptionContainsIgnoreCase(@Param("text") String text);
}