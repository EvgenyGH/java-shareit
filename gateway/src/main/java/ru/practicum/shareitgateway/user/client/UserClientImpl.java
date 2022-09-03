package ru.practicum.shareitgateway.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BasicClient;

@Component
public class UserClientImpl extends BasicClient implements UserClient{
    @Autowired
    public UserClientImpl(@Value("${SERVER_URL}") String url,  RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(url + "/items")).
                requestFactory(HttpComponentsClientHttpRequestFactory::new).
                build());
    }
}
