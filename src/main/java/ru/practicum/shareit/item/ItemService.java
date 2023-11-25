package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentForRequestDto;
import ru.practicum.shareit.item.dto.CommentForResponseDto;
import ru.practicum.shareit.item.dto.ExtendedItemForResponseDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;

import java.util.List;

public interface ItemService {
    ExtendedItemForResponseDto findItem(long userId, long id);

    Item checkItem(long id);

    List<ExtendedItemForResponseDto> findAllItems(long userId, int from, int size);

    ItemForResponseDto createNewItem(long userId, ItemForRequestDto itemDto);

    ItemForResponseDto updateItem(long userId, long id, ItemForRequestDto itemDto);

    List<ItemForResponseDto> searchItem(long userId, String text, int from, int size);

    CommentForResponseDto createNewComment(long userId, long itemId, CommentForRequestDto commentDto);
}
