package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemDaoInMemoryImpl implements ItemDao {
    private Map<Long, Item> items = new HashMap<>();
    private long currentId = 1;

    @Override
    public Optional<Item> find(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAll(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        log.debug("+ create Item: {}", item);

        item.setId(currentId++);
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public void update(Item item) {
        log.debug("+ update Item: {}", item);

        items.put(item.getId(), item);
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::isAvailable)
                .collect(Collectors.toList());
    }
}
