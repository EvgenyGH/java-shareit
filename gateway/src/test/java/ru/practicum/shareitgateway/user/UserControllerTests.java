package ru.practicum.shareitgateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitgateway.user.client.UserClient;
import ru.practicum.shareitserver.user.User;

import javax.validation.ValidationException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTests {
    @MockBean
    private UserClient client;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private User userFirst;
    private final ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.IM_USED);

    @BeforeEach
    void initialize() {
        userFirst = new User(10L, "user name 10", "email@10.com");
    }

    @Test
    void addUserEndpointTest() throws Exception {
        when(client.addUser(userFirst)).thenReturn(response);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userFirst)));

        verify(client, times(1)).addUser(userFirst);
    }

    @Test
    void updateUserEndpointTest() throws Exception {
        when(client.updateUser(userFirst.getId(), userFirst)).thenReturn(response);
        mockMvc.perform(patch("/users/{userId}", userFirst.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userFirst)));

        verify(client, times(1)).updateUser(userFirst.getId(), userFirst);
    }

    @Test
    void getUserByIdEndpointTest() throws Exception {
        when(client.getUserById(userFirst.getId())).thenReturn(response);
        mockMvc.perform(get("/users/{userId}", userFirst.getId()));

        verify(client, times(1)).getUserById(userFirst.getId());
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userFirst.getId()))
                .andExpectAll(status().isOk());

        verify(client, times(1)).deleteUserById(userFirst.getId());
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(client.getAllUsers()).thenReturn(response);

        mockMvc.perform(get("/users"));

        verify(client, times(1)).getAllUsers();
    }

    @Test
    void exceptionHandlerValidationExceptionTests() throws Exception {
        when(client.deleteUserById(anyLong())).thenThrow(ValidationException.class);
        mockMvc.perform(delete("/users/{userId}", userFirst.getId()))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void exceptionHandlerTests() throws Exception {
        when(client.deleteUserById(userFirst.getId())).thenThrow(new RuntimeException());
        mockMvc.perform(delete("/users/{userId}", userFirst.getId()))
                .andExpectAll(status().isInternalServerError());
    }
}
