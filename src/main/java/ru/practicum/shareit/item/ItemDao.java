package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Optional<Item> find(long id);

    List<Item> findAll(long userId);

    Item create(Item item);

    void update(Item item);

    List<Item> search(String text);
}
