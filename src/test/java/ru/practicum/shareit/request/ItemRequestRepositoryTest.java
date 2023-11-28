package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    @DisplayName("Method findAllFromOtherUsers(long userId, Pageable pageable) should return correct list of" +
            " itemRequests")
    void shouldReturnAllRequestsExcludingUsers() throws Exception {
        final List<Long> expectedRequests = List.of(5L, 3L, 2L);

        final List<Long> requests = itemRequestRepository
                .findAllFromOtherUsers(3L, PageRequest.of(0, 20)).stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        assertEquals(expectedRequests, requests, "Requests and expectedRequests is not match");
    }

    @Test
    @DisplayName("Method findAllByRequesterIdOrderByCreatedDesc(long userId) should return correct list of" +
            " itemRequests")
    void shouldReturnAllRequestsOfUser() throws Exception {
        final List<Long> expectedRequests = List.of(4L, 1L);

        final List<Long> requests = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(3L).stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        assertEquals(expectedRequests, requests, "Requests and expectedRequests is not match");
    }
}
