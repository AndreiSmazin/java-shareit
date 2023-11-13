package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemForResponseWithBookingsDto findItem(long userId, long id);

    Item findItem(long id);

    List<ItemForResponseWithBookingsDto> findAllItems(long userId);

    Item createNewItem(long userId, ItemForRequestDto itemDto);

    Item updateItem(long userId, long id, ItemForRequestDto itemDto);

    List<Item> searchItem(long userId, String text);
}
