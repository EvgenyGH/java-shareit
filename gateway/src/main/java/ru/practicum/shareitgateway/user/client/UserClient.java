package ru.practicum.shareitgateway.user.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareitserver.user.User;

public interface UserClient {
    ResponseEntity<Object> addUser(User user);

    ResponseEntity<Object> updateUser(long userId, User user);

    ResponseEntity<Object> getUserById(long userId);

    ResponseEntity<Object> deleteUserById(long userId);

    ResponseEntity<Object> getAllUsers();
}
