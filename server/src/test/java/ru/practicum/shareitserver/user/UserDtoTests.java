package ru.practicum.shareitserver.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.dto.UserDtoMapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTests {
    private UserDto userDto;

    @Autowired
    private JacksonTester<UserDto> jsonDto;

    @BeforeEach
    void initialize() {
        User user = new User(10L, "user name 10", "email@10.com");
        userDto = UserDtoMapper.userToDto(user);
    }

    @Test
    void userDtoJsonTest() throws IOException {
        JsonContent<UserDto> json = jsonDto.write(userDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}
