package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.exception.EmailExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import javax.validation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final Validator validator;

    @Autowired
    public UserService(@Lazy ItemService itemService, UserRepository userRepository) {
        this.itemService = itemService;
        this.userRepository = userRepository;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    //Добавить пользователя
    public User addUser(User user) {
        if (userRepository.findFirstByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new EmailExistsException("Пользователь с таким email уже существует"
                    , Map.of("Object", "User"
                    , "Field", "Email"
                    , "Description", "Duplicates"));
        }

        user.setId(null);
        user = userRepository.save(user);

        log.trace("Пользователь id={} добавлен: {}", user.getId(), user);

        return user;
    }

    //Обновить данные пользователя
    public User updateUser(User user) {
        Optional<User> userStoredOpt = userRepository.findById(user.getId());

        if (userStoredOpt.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден id=" + user.getId()
                    , Map.of("Object", "User"
                    , "Id", String.valueOf(user.getId())
                    , "Description", "Not found"));
        }

        if (user.getName() == null) {
            user.setName(userStoredOpt.get().getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userStoredOpt.get().getEmail());
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

        user = userRepository.save(user);

        log.trace("Данные пользователя id={} обновлены: {}", user.getId(), user);

        return user;
    }

    //Получить пользователя по id
    public User getUserById(long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден id=" + id
                    , Map.of("Object", "User"
                    , "Id", String.valueOf(id)
                    , "Description", "Not found"));
        }

        log.trace("Пользователь id={} отправлен: {}", id, userOpt.get());

        return userOpt.get();
    }

    //Удалить пользователя по id
    @Transactional
    public User deleteUserById(long id) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден id=" + id
                    , Map.of("Object", "User"
                    , "Id", String.valueOf(id)
                    , "Description", "Not found"));
        }

        userRepository.deleteById(id);

        itemService.deleteUserItems(id);

        log.trace("Пользователь id={} удален: {}", id, userOpt.get());

        return userOpt.get();
    }

    //Получить всех пользователей
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();

        log.trace("Все пользователи отправлены. Всего {}.", users.size());

        return users;
    }
}
