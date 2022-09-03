package ru.practicum.shareitgateway.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BasicClient {
    private final RestTemplate restTemplate;
    private final HttpHeaders defaultHeaders;
    // TODO: 03.09.2022 simplify

    public BasicClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.defaultHeaders = new HttpHeaders();
        defaultHeaders.setContentType(MediaType.APPLICATION_JSON);
        defaultHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
    }

    //GET part
    public ResponseEntity<Object> get(Long userId) {
        return makeRequest(userId, "", null, HttpMethod.GET, null);
    }

    public ResponseEntity<Object> get(Long userId, Map<String, Object> params) {

        return makeRequest(userId, "", null, HttpMethod.GET, params);
    }

    public ResponseEntity<Object> get(Long userId, String url, Map<String, Object> params) {
        return makeRequest(userId, url, null, HttpMethod.GET, params);
    }

    //POST part
    public <T> ResponseEntity<Object> post(Long userId, T body) {
        return makeRequest(userId, "", body, HttpMethod.POST, null);
    }

    public <T> ResponseEntity<Object> post(T body) {
        return makeRequest(null, "", body, HttpMethod.POST, null);
    }

    public <T> ResponseEntity<Object> post(Long userId, T body, Map<String, Object> params) {
        return makeRequest(userId, "", body, HttpMethod.POST, params);
    }

    public <T> ResponseEntity<Object> post(T body, Map<String, Object> params) {
        return makeRequest(null, "", body, HttpMethod.POST, params);
    }

    public <T> ResponseEntity<Object> post(Long userId, String url, T body, Map<String, Object> params) {
        return makeRequest(userId, url, body, HttpMethod.POST, params);
    }

    public <T> ResponseEntity<Object> post(String url, T body, Map<String, Object> params) {
        return makeRequest(null, url, body, HttpMethod.POST, params);
    }
    
    //PATCH part
    public <T> ResponseEntity<Object> patch(Long userId, T body) {
        return makeRequest(userId, "", body, HttpMethod.PATCH, null);
    }

    public <T> ResponseEntity<Object> patch(T body) {
        return makeRequest(null, "", body, HttpMethod.PATCH, null);
    }

    public <T> ResponseEntity<Object> patch(Long userId, T body, Map<String, Object> params) {
        return makeRequest(userId, "", body, HttpMethod.PATCH, params);
    }

    public <T> ResponseEntity<Object> patch(T body, Map<String, Object> params) {
        return makeRequest(null, "", body, HttpMethod.PATCH, params);
    }

    public <T> ResponseEntity<Object> patch(Long userId, String url, T body, Map<String, Object> params) {
        return makeRequest(userId, url, body, HttpMethod.PATCH, params);
    }

    public <T> ResponseEntity<Object> patch(String url, T body, Map<String, Object> params) {
        return makeRequest(null, url, body, HttpMethod.PATCH, params);
    }

    //DELETE part
    public ResponseEntity<Object> delete(Long userId, String url, Map<String, Object> params) {
        return makeRequest(userId, url, null, HttpMethod.DELETE, params);
    }

    //Отправка REST запроса и получение ответа
    private <T> ResponseEntity<Object> makeRequest(Long userId, String url, T body, HttpMethod method,
                                                   Map<String, Object> params) {
        HttpEntity<Object> request = new HttpEntity<>(body, getDefaultHeaders(userId));
        ResponseEntity<Object> response;

        try {
            if (params == null) {
                response = restTemplate.exchange(url, method, request, Object.class);
            } else {
                response = restTemplate.exchange(url, method, request, Object.class, params);
            }
        } catch (HttpStatusCodeException exception) {
            response = new ResponseEntity<>(exception.getResponseBodyAsByteArray(),
                    exception.getStatusCode());
        }

        return response;
    }

    //Создание заголовков
    private HttpHeaders getDefaultHeaders(Long userId) {
        if (userId == null) {
            defaultHeaders.remove("X-Sharer-User-Id");
        } else {
            defaultHeaders.set("X-Sharer-User-Id", userId.toString());
        }
        return defaultHeaders;
    }
}
