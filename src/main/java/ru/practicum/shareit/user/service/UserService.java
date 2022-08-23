package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import javax.transaction.Transactional;
import java.util.List;

public interface UserService {
    //Добавить пользователя
    User addUser(User user);

    //Обновить данные пользователя
    User updateUser(User user);

    //Получить пользователя по id
    User getUserById(long id);

    //Удалить пользователя по id
    @Transactional
    User deleteUserById(long id);

    //Получить всех пользователей
    List<User> getAllUsers();
}
