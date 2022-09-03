package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.user.client.UserClient;
import ru.practicum.shareitserver.user.User;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
    private final UserClient client;

    //Добавить пользователя
    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody User user) {
        return client.addUser(user);//userService.addUser(user);
    }

    //Обновить данные пользователя
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @RequestBody User user) {
        return client.updateUser(userId, user); //userService.updateUser(user);
    }

    //Получить пользователя по id
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        return client.getUserById(userId); //userService.getUserById(userId);
    }

    //Удалить пользователя по id
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        return client.deleteUserById(userId); //userService.deleteUserById(userId);
    }

    //Получить всех пользователей
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return client.getAllUsers(); //userService.getAllUsers();
    }
}
