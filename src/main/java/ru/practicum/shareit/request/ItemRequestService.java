package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ExtendedItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;

import java.util.List;

public interface ItemRequestService {
    ExtendedItemRequestForResponseDto findItemRequestById(long userId, long id);

    ItemRequest checkItemRequest(long id);

    List<ExtendedItemRequestForResponseDto> findItemRequestsByUserId(long userId);

    List<ExtendedItemRequestForResponseDto> findAllItemRequests(long userId, int from, int size);

    ItemRequestForResponseDto createNewItemRequest(long userId, ItemRequestDto itemRequestDto);
}
