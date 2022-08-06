package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmailIgnoreCase(String email);
    Optional<User> findFirstByEmailIgnoreCaseAndIdNot(String email, Long id);
}