package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.EmailExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import javax.validation.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final Validator validator;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    //Добавить пользователя
    public User addUser(User user) {
        user.setId(null);
        user = userRepository.save(user);

        log.trace("Пользователь id={} добавлен: {}", user.getId(), user);

        return user;
    }

    //Обновить данные пользователя
    public User updateUser(User user) {
        User userStored = userRepository.findById(user.getId())
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь не найден id=" + user.getId()
                                , Map.of("Object", "User"
                                , "Id", String.valueOf(user.getId())
                                , "Description", "Not found")));

        if (user.getName() == null) {
            user.setName(userStored.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userStored.getEmail());
        }

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }

        if (userRepository.findFirstByEmailIgnoreCaseAndIdNot(user.getEmail(), user.getId()).isPresent()) {
            throw new EmailExistsException("Пользователь с таким email уже существует"
                    , Map.of("Object", "User"
                    , "Field", "Email"
                    , "Value", user.getEmail()
                    , "Description", "Duplicates"));
        }

        User userSaved = userRepository.save(user);

        log.trace("Данные пользователя id={} обновлены: {}", user.getId(), userSaved);

        return userSaved;
    }

    //Получить пользователя по id
    public User getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден id=" + id
                        , Map.of("Object", "User"
                        , "Id", String.valueOf(id)
                        , "Description", "Not found")));

        log.trace("Пользователь id={} отправлен: {}", id, user);

        return user;
    }

    //Удалить пользователя по id
    @Transactional
    public User deleteUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден id=" + id
                        , Map.of("Object", "User"
                        , "Id", String.valueOf(id)
                        , "Description", "Not found")));

        userRepository.deleteById(id);

        log.trace("Пользователь id={} удален: {}", id, user);

        return user;
    }

    //Получить всех пользователей
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();

        log.trace("Все пользователи отправлены. Всего {}.", users.size());

        return users;
    }
}
