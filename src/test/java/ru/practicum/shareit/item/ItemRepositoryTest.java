package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("Method findAllByOwnerIdOrderById(long ownerId, Pageable pageable) should return correct list of" +
            " items")
    void shouldReturnAllItemsByOwnerId() throws Exception {
        final List<Long> expectedItemsIds = List.of(1L, 3L, 4L);

        final List<Long> itemsIds = itemRepository
                .findAllByOwnerIdOrderById(1L, PageRequest.of(0, 20)).stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        assertEquals(expectedItemsIds, itemsIds, "ItemsIds and expectedItemsIds is not match");
    }

    @Test
    @DisplayName("Method findItemsByNameOrDescription(String text, Pageable pageable) should return correct list of" +
            " items")
    void shouldReturnAllItemsByNameOrDescription() throws Exception {
        final List<Long> expectedItemsIdsForName = List.of(1L, 2L);

        final List<Long> itemsIdsForName = itemRepository
                .findItemsByNameOrDescription("Дрель", PageRequest.of(0, 20)).stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        assertEquals(expectedItemsIdsForName, itemsIdsForName, "ItemsIdsForName and expectedItemsIdsForName" +
                " is not match");

        final List<Long> expectedItemsIdsForDescription = List.of(5L);

        final List<Long> itemsIdsForDescription = itemRepository
                .findItemsByNameOrDescription("для плотной ткани", PageRequest.of(0, 20)).stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        assertEquals(expectedItemsIdsForDescription, itemsIdsForDescription, "ItemsIdsForDescription and" +
                " expectedItemsIdsForDescription is not match");
    }
}
