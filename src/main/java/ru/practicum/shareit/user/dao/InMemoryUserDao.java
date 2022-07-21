package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.EmailExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.validation.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
@Slf4j
public class InMemoryUserDao implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long id = 0;
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    //Добавить пользователя
    @Override
    public User addUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new EmailExistsException("Пользователь с таким email уже существует"
                    , Map.of("Object", "User"
                    , "Field", "Email"
                    , "Description", "Duplicates"));
        }

        user.setId(++id);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        log.trace("Пользователь id={} добавлен: {}", user.getId(), user);

        return user;
    }

    //Обновить данные пользователя
    @Override
    public User updateUser(User user) {
        User tempUser = users.get(user.getId());

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
        } else {
            emails.remove(tempUser.getEmail());

            if (emails.contains(user.getEmail())) {
                emails.add(tempUser.getEmail());
                throw new EmailExistsException("Пользователь с таким email уже существует"
                        , Map.of("Object", "User"
                        , "Field", "Email"
                        , "Value", user.getEmail()
                        , "Description", "Duplicates"));
            }
        }

        validateUser(user);
        users.put(user.getId(), user);
        emails.add(user.getEmail());

        return user;
    }

    private void validateUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            emails.add(user.getEmail());
            throw new ConstraintViolationException(violations);
        }
    }

    //Получить пользователя по id
    @Override
    public User getUserById(long id) {
        User tempUser = users.get(id);

        if (tempUser == null) {
            throw new UserNotFoundException("Пользователь не найден id=" + id
                    , Map.of("Object", "User"
                    , "Id", String.valueOf(id)
                    , "Description", "Not found"));
        }

        return tempUser;
    }

    //Удалить пользователя по id
    @Override
    public User deleteUserById(long id) {
        if (users.containsKey(id)) {
            User tempUser = users.get(id);
            emails.remove(tempUser.getEmail());
            return tempUser;
        } else {
            throw new UserNotFoundException("Пользователь не найден id=" + id
                    , Map.of("Object", "User"
                    , "Id", String.valueOf(id)
                    , "Description", "Not found"));
        }
    }
}
