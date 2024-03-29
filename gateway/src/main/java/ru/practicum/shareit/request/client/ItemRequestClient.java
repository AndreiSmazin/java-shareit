package ru.practicum.shareit.request.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.Map;

@Service
@Slf4j
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getItemRequestsByUserId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllItemRequests(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of("from", from,  "size", size);

        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemRequest(long userId, long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> createNewItemRequest(long userId, ItemRequestCreateDto itemRequestCreateDto) {
        log.debug("+ createNewItemRequest: {}, {}", userId, itemRequestCreateDto);

        return post("", userId, itemRequestCreateDto);
    }
}
