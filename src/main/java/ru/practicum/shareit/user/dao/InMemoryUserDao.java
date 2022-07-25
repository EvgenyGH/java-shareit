package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class InMemoryUserDao implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long id = 0;

    //Добавить пользователя
    @Override
    public User addUser(User user) {
        user.setId(++id);
        updateUser(user);

        return user;
    }

    //Обновить данные пользователя
    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        emails.add(user.getEmail());

        return user;
    }

    //Получить пользователя по id
    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    //Удалить пользователя по id
    @Override
    public User deleteUserById(long id) {
        User user = users.get(id);
        emails.remove(user.getEmail());
        users.remove(id);

        return user;
    }

    //Получить всех пользователей
    public Collection<User> getAllUsers() {
        return users.values();
    }

    //Получить все адреса почты
    public Set<String> getAllEmails() {
        return emails;
    }

    //Добавить адрес почты
    public String addEmail(String email) {
        emails.add(email);

        return email;
    }

    //Удалить адрес почты
    public String removeEmail(String email) {
        emails.remove(email);

        return email;
    }
}
