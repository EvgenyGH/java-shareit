package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    //Добавить пользователя
    public User addUser(User user){
        return userDao.addUser(user);
    }

    //Обновить данные пользователя
    public User updateUser(User user){
        return userDao.updateUser(user);
    }

    //Получить пользователя по id
    public User getUserById(long id){
        return userDao.getUserById(id);
    }

    //Удалить пользователя по id
    public User deleteUserById(long id){
        return userDao.deleteUserById(id);
    }
}
