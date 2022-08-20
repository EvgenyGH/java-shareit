package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.exception.EmailExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;


    @BeforeEach
    void initialize() {
        user = new User(10L, "user name 10", "email@10.com");
    }

    @Test
    void addUserTest() {
        when(userRepository.save(user)).thenReturn(user);
        assertThat(userService.addUser(user)).isEqualTo(user);
    }

    @Test
    void updateUserTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findFirstByEmailIgnoreCaseAndIdNot(user.getEmail(), user.getId()))
                .thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        assertThat(userService.updateUser(user)).isEqualTo(user);
    }

    @Test
    void updateUserNotFoundExceptionTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(user)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUserEmailExistsExceptionTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findFirstByEmailIgnoreCaseAndIdNot(user.getEmail(), user.getId()))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.updateUser(user)).isInstanceOf(EmailExistsException.class);
    }

    @Test
    void updateUserConstraintViolationExceptionTest() {
        user.setName("");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> userService.updateUser(user)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        assertThat(userService.getUserById(user.getId())).isEqualTo(user);
    }

    @Test
    void getUserByIdUserNotFoundExceptionTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(()->userService.getUserById(user.getId())).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deleteUserByIdTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deleteUserById(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void deleteUserByIdUserNotFoundExceptionTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(()->userService.deleteUserById(user.getId())).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        assertThat(userService.getAllUsers()).isEqualTo(List.of(user));
    }
}
