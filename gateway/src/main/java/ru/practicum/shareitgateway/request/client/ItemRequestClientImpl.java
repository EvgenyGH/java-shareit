package ru.practicum.shareitgateway.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BasicClient;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;

import java.util.Map;
import java.util.Optional;

@Component
public class ItemRequestClientImpl extends BasicClient implements ItemRequestClient {
    @Autowired
    public ItemRequestClientImpl(@Value("${shareit.server.url}") String url, RestTemplateBuilder builder) {
        super(builder
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .uriTemplateHandler(new DefaultUriBuilderFactory(url + "/requests"))
                .build());
    }

    @Override
    public ResponseEntity<Object> addRequest(Long userId, ItemRequestDto itemRequestDto) {
        return post(userId, itemRequestDto);
    }

    @Override
    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get(userId);
    }

    @Override
    public ResponseEntity<Object> getAllRequests(Long ownerId, Optional<Integer> from, Optional<Integer> size) {
        Map<String, Object> params = Map.of(
                "from", from.isPresent() ? from.get() : "",
                "size", size.isPresent() ? size.get() : ""
        );
        return get(ownerId, "/all?from={from}&size={size}", params);
    }

    @Override
    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get(userId, "/" + requestId);
    }
}
