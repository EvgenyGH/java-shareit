package ru.practicum.shareitserver.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationUserServiceAndDbTests {
    private final UserService userService;
    private final EntityManager manager;

    private User userFirst;
    private User userSecond;

    @BeforeEach
    void initialize() {
        userFirst = new User(10L, "user name 10", "email@10.com");
        userSecond = new User(20L, "user name 20", "email@20.com");
    }

    @Test
    @Transactional
    void addUserTest() {
        userFirst = userService.addUser(userFirst);
        User userDb = manager.createQuery("SELECT u FROM User u " +
                        "WHERE u.id = :id", User.class)
                .setParameter("id", userFirst.getId()).getSingleResult();

        assertThat(userFirst).isEqualTo(userDb);
    }

    @Test
    @Transactional
    void updateUserTest() {
        userFirst = userService.addUser(userFirst);
        userFirst.setName("Super NAME");
        userService.updateUser(userFirst);
        User userDb = manager.createQuery("SELECT u FROM User u " +
                        "WHERE u.id = :id", User.class)
                .setParameter("id", userFirst.getId()).getSingleResult();

        assertThat(userFirst).isEqualTo(userDb);
    }

    @Test
    @Transactional
    void getUserByIdTest() {
        userFirst = userService.addUser(userFirst);
        User userDb = userService.getUserById(userFirst.getId());
        assertThat(userFirst).isEqualTo(userDb);
    }

    @Test
    @Transactional
    void deleteUserByIdTest() {
        userFirst = userService.addUser(userFirst);
        userService.deleteUserById(userFirst.getId());

        TypedQuery<User> query = manager.createQuery("SELECT u FROM User u " +
                        "WHERE u.id = :id", User.class)
                .setParameter("id", userFirst.getId());

        assertThat(query.getResultList().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    void getAllUsersTest() {
        userFirst = userService.addUser(userFirst);
        userSecond = userService.addUser(userSecond);
        assertThat(userService.getAllUsers().size()).isEqualTo(2);
    }
}
