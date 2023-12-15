package ru.practicum.shareit.item.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateUpdateDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getItem(long userId, long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAllItems(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of("from", from,  "size", size);

        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchItems(long userId, String text, int from, int size) {
        Map<String, Object> parameters = Map.of("text", text, "from", from,  "size", size);

        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createNewItem(long userId, ItemCreateUpdateDto itemCreateUpdateDto) {
        log.debug("+ createNewItem: {}, {}", userId, itemCreateUpdateDto);

        return post("", userId, itemCreateUpdateDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long id, ItemCreateUpdateDto itemCreateUpdateDto) {
        log.debug("+ updateItem: {}, {}, {}", userId, id, itemCreateUpdateDto);

        return patch("/" + id, userId, itemCreateUpdateDto);
    }

    public ResponseEntity<Object> createNewComment(long userId, long id, CommentCreateDto commentCreateDto) {
        log.debug("+ createNewComment: {}, {}, {}", userId, id, commentCreateDto);

        return post("/" + id + "/comment", userId, commentCreateDto);
    }
}
