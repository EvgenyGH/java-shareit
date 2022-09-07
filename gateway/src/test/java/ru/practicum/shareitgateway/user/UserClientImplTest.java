package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareitgateway.user.client.UserClientImpl;
import ru.practicum.shareitserver.user.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"shareit.server.url=http://localhost:8080"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserClientImplTest {
    @SpyBean
    private final UserClientImpl client;
    private static ResponseEntity<Object> response;
    private static User user;

    @BeforeAll
    static void initialize() {
        response = new ResponseEntity<>(HttpStatus.IM_USED);
        user = new User(1L, "name", "mail@mail.ru");
    }

    @Test
    void whenAddUserThenCallPostWithBodyOnceAndReturnResponse() {
        doReturn(response).when(client).post(user);
        assertThat(client.addUser(user)).isEqualTo(response);
        verify(client, times(1)).post(user);
    }

    @Test
    void whenUpdateUserThenCallPatchWithUserIdAndUrlAndBodyOnceAndReturnResponse() {
        doReturn(response).when(client).patch(user.getId(), "/" + user.getId(), user);
        assertThat(client.updateUser(user.getId(), user)).isEqualTo(response);
        verify(client, times(1)).patch(user.getId(), "/" + user.getId(), user);
    }

    @Test
    void whenGetUserByIdThenCallGetWithUserIdAndUrlOnceAndReturnResponse() {
        doReturn(response).when(client).get(user.getId(), "/" + user.getId());
        assertThat(client.getUserById(user.getId())).isEqualTo(response);
        verify(client, times(1)).get(user.getId(), "/" + user.getId());
    }

    @Test
    void whenDeleteUserByIdThenCallDeleteWithUserIdAndUrlOnceAndReturnResponse() {
        doReturn(response).when(client).delete(user.getId(), "/" + user.getId());
        assertThat(client.deleteUserById(user.getId())).isEqualTo(response);
        verify(client, times(1)).delete(user.getId(), "/" + user.getId());
    }

    @Test
    void whenGetAllUsersThenCallGetOnceAndReturnResponse() {
        doReturn(response).when(client).get();
        assertThat(client.getAllUsers()).isEqualTo(response);
        verify(client, times(1)).get();
    }
}