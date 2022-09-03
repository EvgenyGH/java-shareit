package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.user.client.UserClient;
import ru.practicum.shareitserver.user.User;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {
    private final UserClient client;

    //Добавить пользователя
    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody User user) {
        log.trace("Gateway: addUser: {}", user);
        return client.addUser(user);
    }

    //Обновить данные пользователя
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @RequestBody User user) {
        log.trace("Gateway: updateUser: userId={} user={}", userId, user);
        return client.updateUser(userId, user);
    }

    //Получить пользователя по id
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.trace("Gateway: getUserById: userId={}", userId);
        return client.getUserById(userId);
    }

    //Удалить пользователя по id
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        log.trace("Gateway: deleteUserById: userId={}", userId);
        return client.deleteUserById(userId);
    }

    //Получить всех пользователей
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.trace("Gateway: getAllUsers");
        return client.getAllUsers();
    }
}
