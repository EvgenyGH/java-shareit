package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @PatchMapping
    public User updateUser(@Valid @RequestParam long userId, User user) {
        user.setId(userId);
        return userService.updateUser(user);
    }

    //Получить пользователя по id
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public User getUserById(@RequestParam long userId) {
        return userService.getUserById(userId);
    }

    //Удалить пользователя по id
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    User deleteUserById(@RequestParam long userId) {
        return userService.deleteUserById(userId);
    }
}
