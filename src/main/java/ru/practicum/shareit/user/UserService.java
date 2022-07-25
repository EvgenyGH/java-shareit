package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.exception.EmailExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.validation.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserDao userDao;
    private long id = 0;
    private final ItemService itemService;
    private final Validator validator;

    @Autowired
    public UserService(@Lazy ItemService itemService, UserDao userDao) {
        this.itemService = itemService;
        this.userDao = userDao;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    //Добавить пользователя
    public User addUser(User user) {
        if (userDao.getAllEmails().contains(user.getEmail())) {
            throw new EmailExistsException("Пользователь с таким email уже существует"
                    , Map.of("Object", "User"
                    , "Field", "Email"
                    , "Description", "Duplicates"));
        }

        user.setId(++id);
        userDao.addUser(user);

        log.trace("Пользователь id={} добавлен: {}", user.getId(), user);

        return user;
    }

    //Обновить данные пользователя
    public User updateUser(User user) {
        User tempUser = userDao.getUserById(user.getId());

        if (tempUser == null) {
            throw new UserNotFoundException("Пользователь не найден id=" + user.getId()
                    , Map.of("Object", "User"
                    , "Id", String.valueOf(user.getId())
                    , "Description", "Not found"));
        }

        if (user.getName() == null) {
            user.setName(tempUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(tempUser.getEmail());
        }

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }

        userDao.removeEmail(tempUser.getEmail());

        if (userDao.getAllEmails().contains(user.getEmail())) {
            userDao.addEmail(tempUser.getEmail());
            throw new EmailExistsException("Пользователь с таким email уже существует"
                    , Map.of("Object", "User"
                    , "Field", "Email"
                    , "Value", user.getEmail()
                    , "Description", "Duplicates"));
        }

        userDao.addUser(user);

        log.trace("Данные пользователя id={} обновлены: {}", user.getId(), user);

        return user;
    }

    //Получить пользователя по id
    public User getUserById(long id) {
        User user = userDao.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден id=" + id
                    , Map.of("Object", "User"
                    , "Id", String.valueOf(id)
                    , "Description", "Not found"));
        }

        log.trace("Пользователь id={} отправлен: {}", id, user);

        return user;
    }

    //Удалить пользователя по id
    public User deleteUserById(long id) {
        User user = userDao.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден id=" + id
                    , Map.of("Object", "User"
                    , "Id", String.valueOf(id)
                    , "Description", "Not found"));
        }

        userDao.deleteUserById(id);
        itemService.deleteUserItems(id);

        log.trace("Пользователь id={} удален: {}", id, user);

        return user;
    }

    //Получить всех пользователей
    public Collection<User> getAllUsers() {
        Collection<User> users = userDao.getAllUsers();

        log.trace("Все пользователи отправлены. Всего {}.", users.size());

        return users;
    }
}
