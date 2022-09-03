package ru.practicum.shareitgateway.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BasicClient;
import ru.practicum.shareitserver.user.User;

@Component
public class UserClientImpl extends BasicClient implements UserClient {
    @Autowired
    public UserClientImpl(@Value("${SERVER_URL}") String url, RestTemplateBuilder builder) {
        super(builder.
                requestFactory(HttpComponentsClientHttpRequestFactory.class).
                uriTemplateHandler(new DefaultUriBuilderFactory(url + "/users")).
                build());
    }

    @Override
    public ResponseEntity<Object> addUser(User user) {
        return post(user);
    }

    @Override
    public ResponseEntity<Object> updateUser(long userId, User user) {
        return patch(userId, "/" + userId, user, null);
    }

    @Override
    public ResponseEntity<Object> getUserById(long userId) {
        return get(userId, "/" + userId, null);
    }

    @Override
    public ResponseEntity<Object> deleteUserById(long userId) {
        return delete(userId, "/" + userId, null);
    }

    @Override
    public ResponseEntity<Object> getAllUsers() {
        return get(null);
    }
}
