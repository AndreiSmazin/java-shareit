package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentForRequestDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ExtendedItemForResponseDto;

import java.util.List;

public interface ItemService {
    ExtendedItemForResponseDto findItem(long userId, long id);

    Item findItem(long id);

    List<ExtendedItemForResponseDto> findAllItems(long userId);

    Item createNewItem(long userId, ItemForRequestDto itemDto);

    Item updateItem(long userId, long id, ItemForRequestDto itemDto);

    List<Item> searchItem(long userId, String text);

    Comment createNewComment(long userId, long itemId, CommentForRequestDto commentDto);
}
