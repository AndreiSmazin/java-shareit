package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface ItemService {
    ItemExtendedResponseDto findItem(long userId, long id);

    Item checkItem(long id);

    List<ItemExtendedResponseDto> findAllItems(long userId, int from, int size);

    ItemResponseDto createNewItem(long userId, ItemCreateUpdateDto itemDto);

    ItemResponseDto updateItem(long userId, long id, ItemCreateUpdateDto itemDto);

    List<ItemResponseDto> searchItem(long userId, String text, int from, int size);

    CommentResponseDto createNewComment(long userId, long itemId, CommentCreateDto commentDto);
}
