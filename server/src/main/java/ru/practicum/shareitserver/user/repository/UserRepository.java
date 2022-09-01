package ru.practicum.shareitserver.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareitserver.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmailIgnoreCaseAndIdNot(String email, Long id);
}
