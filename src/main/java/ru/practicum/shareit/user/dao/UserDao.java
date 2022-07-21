package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserDao {
    //Добавить пользователя
    User addUser(User user);

    //Обновить данные пользователя
    User updateUser(User user);

    //Получить пользователя по id
    User getUserById(long id);

    //Удалить пользователя по id
    User deleteUserById(long id);

    //Получить всех пользователей
    Collection<User> getAllUsers();
}
