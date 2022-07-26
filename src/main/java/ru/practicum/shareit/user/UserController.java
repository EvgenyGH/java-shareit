package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //Добавить пользователя
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    //Обновить данные пользователя
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable long userId, @RequestBody User user) {
        user.setId(userId);
        return userService.updateUser(user);
    }

    //Получить пользователя по id
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    //Удалить пользователя по id
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{userId}")
    public User deleteUserById(@PathVariable long userId) {
        return userService.deleteUserById(userId);
    }

    //Получить всех пользователей
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
