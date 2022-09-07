package ru.practicum.shareitgateway.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitserver.user.User;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasicClientTest {
    private final RestTemplate template;
    private final BasicClient client;
    private final ResponseEntity<Object> response;
    private final Map<String, Object> params;
    private final String url;
    private final User user;

    public BasicClientTest() {
        this.template = spy(RestTemplate.class);
        this.client = spy(new BasicClient(template));
        this.params = Map.of("param1", 1, "param2", 2);
        this.url = "url";
        this.response = new ResponseEntity<>(HttpStatus.IM_USED);
        this.user = new User(1L, "name", "mail@mail.ru");
    }

    //GET part
    @Test
    void whenGetThenReturnRequestAndMakeRequestOnce() {
        doReturn(response).when(client).makeRequest(null, "", null, HttpMethod.GET, null);
        assertThat(client.get()).isEqualTo(response);
        verify(client, times(1)).get();
    }

    @Test
    void whenGetUserIdThenReturnRequestAndMakeRequestOnce() {
        doReturn(response).when(client).makeRequest(1L, "", null, HttpMethod.GET, null);
        assertThat(client.get(1L)).isEqualTo(response);
        verify(client, times(1)).get(1L);
    }

    @Test
    void whenGetUserIdAndUrlThenReturnRequestAndMakeRequestOnce() {
        doReturn(response).when(client).makeRequest(1L, url, null, HttpMethod.GET, null);
        assertThat(client.get(1L, url)).isEqualTo(response);
        verify(client, times(1)).get(1L, url);
    }

    @Test
    void whenGetUserIdAndUrlAndPramsThenReturnRequestAndMakeRequestOnce() {
        doReturn(response).when(client).makeRequest(1L, url, null, HttpMethod.GET, params);
        assertThat(client.get(1L, url, params)).isEqualTo(response);
        verify(client, times(1)).get(1L, url, params);
    }

    //POST part
    @Test
    void whenPostBodyThenReturnRequestAndMakeRequestOnce() {
        doReturn(response).when(client).makeRequest(null, "", user, HttpMethod.POST, null);
        assertThat(client.post(user)).isEqualTo(response);
        verify(client, times(1)).post(user);
    }

    @Test
    void whenPostUserIdAndBodyThenReturnRequestAndMakeRequestOnce() {
        doReturn(response).when(client).makeRequest(1L, "", user, HttpMethod.POST, null);
        assertThat(client.post(1L, user)).isEqualTo(response);
        verify(client, times(1)).post(1L, user);
    }

    @Test
    void whenPostUserIdAndUrlAndBodyThenReturnRequestAndMakeRequestOnce() {
        doReturn(response).when(client).makeRequest(1L, url, user, HttpMethod.POST, null);
        assertThat(client.post(1L, url, user)).isEqualTo(response);
        verify(client, times(1)).post(1L, url, user);
    }

    //PATCH part
    @Test
    void whenPatchUserIdAndUrlAndBodyUserIdAndUrlAndBodyThenReturnRequestAndMakeRequestOnce() {
        doReturn(response).when(client).makeRequest(1L, url, user, HttpMethod.PATCH, null);
        assertThat(client.patch(1L, url, user)).isEqualTo(response);
        verify(client, times(1)).patch(1L, url, user);
    }

    @Test
    void whenPatchUserIdAndUrlAndBodyAndParamsThenReturnRequestAndMakeRequestOnce() {
        doReturn(response).when(client).makeRequest(1L, url, user, HttpMethod.PATCH, params);
        assertThat(client.patch(1L, url, user, params)).isEqualTo(response);
        verify(client, times(1)).patch(1L, url, user, params);
    }

    //DELETE part
    @Test
    void whenDeleteUserIdAndUrlThenReturnRequestAndMakeRequestOnce() {
        doReturn(response).when(client).makeRequest(1L, url, null, HttpMethod.DELETE, null);
        assertThat(client.delete(1L, url)).isEqualTo(response);
        verify(client, times(1)).delete(1L, url);
    }

    //Создание запросов
    @Test
    void whenGetUserIdAndUrlThenCallExchangeOnce() {
        doReturn(response).when(template).exchange(any(String.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class));
        assertThat(client.get(1L, url)).isEqualTo(response);
        verify(template, times(1)).exchange(any(String.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class));
    }

    @Test
    void whenGetUserIdAndUrlAndBodyThenCallExchangeOnce() {
        doReturn(response).when(template).exchange(any(String.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class), any(Map.class));
        assertThat(client.get(1L, url, params)).isEqualTo(response);
        verify(template, times(1)).exchange(any(String.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class), any(Map.class));
    }

    @Test
    void whenGetUserIdAndUrlAndBodyThenThrowHttpStatusCodeException() {
        doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(template).exchange(any(String.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class), any(Map.class));
        assertThat(client.get(1L, url, params).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(template, times(1)).exchange(any(String.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class), any(Map.class));
    }
}
