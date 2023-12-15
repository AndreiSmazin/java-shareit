package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestExtendedResponseDto findItemRequestById(long userId, long id);

    ItemRequest checkItemRequest(long id);

    List<ItemRequestExtendedResponseDto> findItemRequestsByUserId(long userId);

    List<ItemRequestExtendedResponseDto> findAllItemRequests(long userId, int from, int size);

    ItemRequestResponseDto createNewItemRequest(long userId, ItemRequestCreateDto itemRequestCreateDto);
}
