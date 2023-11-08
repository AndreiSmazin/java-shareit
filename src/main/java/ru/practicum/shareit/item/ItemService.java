package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.util.List;

public interface ItemService {
    Item findItem(long userId, long id);

    List<Item> findAllItems(long userId);

    Item createNewItem(long userId, ItemForRequestDto itemDto);

    Item updateItem(long userId, long id, ItemForRequestDto itemDto);

    List<Item> searchItem(long userId, String text);
}
