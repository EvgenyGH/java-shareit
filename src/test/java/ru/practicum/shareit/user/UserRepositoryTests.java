package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    private User userFirst;
    private User userSecond;

    @BeforeEach
    void initialize() {
        userFirst = new User(10L, "user name 10", "email@10.com");
        userSecond = new User(20L, "user name 20", "email@20.com");
    }

    @Test
    @Transactional
    void findFirstByEmailIgnoreCaseAndIdNotTest() {
        userFirst = userRepository.save(userFirst);
        userSecond.setEmail(userFirst.getEmail());
        userSecond = userRepository.save(userSecond);

        Optional<User> user = userRepository.findFirstByEmailIgnoreCaseAndIdNot(userFirst.getEmail(),
                userFirst.getId());

        assertThat(user.orElse(null)).isEqualTo(userSecond);
    }
}
