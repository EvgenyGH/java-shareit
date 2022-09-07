package ru.practicum.shareitserver.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitserver.user.exception.EmailExistsException;
import ru.practicum.shareitserver.user.exception.UserNotFoundException;
import ru.practicum.shareitserver.user.service.UserService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTests {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private User userFirst;
    private User userSecond;

    @BeforeEach
    void initialize() {
        userFirst = new User(10L, "user name 10", "email@10.com");
        userSecond = new User(20L, "user name 20", "email@20.com");
    }

    @Test
    void addUserEndpointTest() throws Exception {
        when(userService.addUser(userFirst)).thenReturn(userFirst);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userFirst))).andExpectAll(
                status().isCreated(), content().json(mapper.writeValueAsString(userFirst)));
    }

    @Test
    void updateUserEndpointTest() throws Exception {
        when(userService.updateUser(userFirst)).thenReturn(userFirst);
        mockMvc.perform(patch("/users/{userId}", userFirst.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userFirst)))
                .andExpectAll(status().isOk(),
                        content().json(mapper.writeValueAsString(userFirst)));
    }

    @Test
    void getUserByIdEndpointTest() throws Exception {
        when(userService.getUserById(userFirst.getId())).thenReturn(userFirst);
        mockMvc.perform(get("/users/{userId}", userFirst.getId()))
                .andExpectAll(status().isOk(),
                        content().json(mapper.writeValueAsString(userFirst)));
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userFirst.getId()))
                .andExpectAll(status().isOk());

        verify(userService, times(1)).deleteUserById(userFirst.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userFirst, userSecond));

        mockMvc.perform(get("/users")).andExpectAll(status().isOk(),
                content().json(mapper.writeValueAsString(List.of(userFirst, userSecond))));
    }

    @Test
    void userExceptionHandlerValidationExceptionTests() throws Exception {
        when(userService.deleteUserById(anyLong())).thenThrow(ValidationException.class);
        mockMvc.perform(delete("/users/{userId}", userFirst.getId()))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void userExceptionHandlerUserNotFoundExceptionTests() throws Exception {
        when(userService.deleteUserById(userFirst.getId()))
                .thenThrow(new UserNotFoundException("message", Map.of("Id", "2")));
        mockMvc.perform(delete("/users/{userId}", userFirst.getId()))
                .andExpectAll(status().isNotFound());
    }

    @Test
    void userExceptionHandlerEmailExistsExceptionTests() throws Exception {
        when(userService.deleteUserById(userFirst.getId()))
                .thenThrow(new EmailExistsException("message", Map.of("Id", "2")));
        mockMvc.perform(delete("/users/{userId}", userFirst.getId()))
                .andExpectAll(status().isConflict());
    }

    @Test
    void userExceptionHandlerTests() throws Exception {
        when(userService.deleteUserById(userFirst.getId())).thenThrow(new RuntimeException());
        mockMvc.perform(delete("/users/{userId}", userFirst.getId()))
                .andExpectAll(status().isInternalServerError());
    }
}
