package ru.practicum.shareitgateway.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitgateway.user.client.UserClientImpl;
import ru.practicum.shareitserver.user.User;

import static org.mockserver.model.HttpRequest.request;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"shareit.server.url=http://localhost:7070"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BasicClientRequestTest {
    private final UserClientImpl client;
    private static ClientAndServer server;
    private static MockServerClient mock;
    private static User user;
    private static Header headerContent;
    private static Header headerAccept;
    private static Header headerUserId;
    private final ObjectMapper wrapper;

    @BeforeAll
    static void initialize() {
        server = ClientAndServer.startClientAndServer(7070);
        mock = new MockServerClient("localhost", 7070);
        user = new User(1L, "name", "mail@mail.ru");
        headerContent = new Header("Content-Type", "application/json");
        headerAccept = new Header("Accept", "application/json");
        headerUserId = new Header("X-Sharer-User-Id", user.getId().toString());
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void whenCallGetThenGetRequestSent() {
        client.get();
        mock.verify(request().withMethod("GET").withPath("/users")
                        .withHeader(headerContent)
                        .withHeader(headerAccept),
                VerificationTimes.exactly(1)
        );
    }

    @Test
    void whenCallPostThenGetRequestSent() throws JsonProcessingException {
        client.post(user.getId(), user);
        mock.verify(request().withMethod("POST").withPath("/users")
                        .withHeader(headerContent)
                        .withHeader(headerAccept)
                        .withHeader(headerUserId)
                        .withBody(wrapper.writeValueAsString(user)),
                VerificationTimes.exactly(1)
        );
    }

    @Test
    void whenCallPatchThenGetRequestSent() throws JsonProcessingException {
        client.patch(user.getId(), "/update", user);
        mock.verify(request().withMethod("PATCH").withPath("/users/update")
                        .withHeader(headerContent)
                        .withHeader(headerAccept)
                        .withHeader(headerUserId)
                        .withBody(wrapper.writeValueAsString(user)),
                VerificationTimes.exactly(1)
        );
    }

    @Test
    void whenCallDeleteThenGetRequestSent() {
        client.delete(user.getId(), "/delete");
        mock.verify(request().withMethod("DELETE").withPath("/users/delete")
                        .withHeader(headerContent)
                        .withHeader(headerAccept)
                        .withHeader(headerUserId),
                VerificationTimes.exactly(1)
        );
    }
}
