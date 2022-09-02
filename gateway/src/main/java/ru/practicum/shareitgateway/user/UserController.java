package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.user.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    //Добавить пользователя
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return null;//userService.addUser(user);
    }

    //Обновить данные пользователя
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable long userId, @RequestBody User user) {
        user.setId(userId);
        return null; //userService.updateUser(user);
    }

    //Получить пользователя по id
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        return null; //userService.getUserById(userId);
    }

    //Удалить пользователя по id
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{userId}")
    public User deleteUserById(@PathVariable long userId) {
        return null; //userService.deleteUserById(userId);
    }

    //Получить всех пользователей
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<User> getAllUsers() {
        return null; //userService.getAllUsers();
    }
}
