package ru.practicum.shareit.booking.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getBooking(long userId, long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAllBookingsByUserId(long userId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of("state", state, "from", from,  "size", size);

        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsByOwnerId(long userId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of("state", state, "from", from,  "size", size);

        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createNewBooking(long userId, BookingCreateDto bookingCreateDto) {
        log.debug("+ createNewBooking: {}, {}", userId, bookingCreateDto);

        return post("", userId, bookingCreateDto);
    }

    public ResponseEntity<Object> updateBookingStatus(long userId, long id, boolean approved) {
        log.debug("+ updateBookingStatus: {}, {}, {}", userId, id, approved);

        Map<String, Object> parameters = Map.of("approved", approved);

        return patch("/" + id + "?approved={approved}", userId, parameters);
    }
}
