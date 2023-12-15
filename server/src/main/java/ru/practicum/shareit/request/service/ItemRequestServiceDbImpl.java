package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceDbImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestExtendedResponseDto findItemRequestById(long userId, long id) {
        userService.checkUser(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("ItemRequest with id %s not exist", id)));

        ItemRequestExtendedResponseDto itemRequestDto = itemRequestMapper
                .itemRequestToExtendedItemRequestForResponseDto(itemRequest);
        addItems(itemRequestDto);

        return itemRequestDto;
    }

    @Override
    public ItemRequest checkItemRequest(long id) {
        return itemRequestRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("ItemRequest with id %s not exist", id)));
    }

    @Override
    public List<ItemRequestExtendedResponseDto> findItemRequestsByUserId(long userId) {
        userService.checkUser(userId);

        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(itemRequestMapper::itemRequestToExtendedItemRequestForResponseDto)
                .peek(this::addItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestExtendedResponseDto> findAllItemRequests(long userId, int from, int size) {
        userService.checkUser(userId);

        return itemRequestRepository.findAllFromOtherUsers(userId, PageRequest.of(from, size)).stream()
                .map(itemRequestMapper::itemRequestToExtendedItemRequestForResponseDto)
                .peek(this::addItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto createNewItemRequest(long userId, ItemRequestCreateDto itemRequestCreateDto) {
        log.debug("+ createNewItemRequest: {}, {}", userId, itemRequestCreateDto);

        ItemRequest itemRequest = itemRequestMapper.itemRequestDtoToItemRequest(itemRequestCreateDto);
        itemRequest.setRequester(userService.checkUser(userId));
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequestMapper.itemRequestToItemRequestForResponseDto(itemRequestRepository.save(itemRequest));
    }

    private void addItems(ItemRequestExtendedResponseDto itemRequest) {
        List<ItemResponseDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                .map(itemMapper::itemToItemForResponseDto)
                .collect(Collectors.toList());

        itemRequest.setItems(items);
    }
}
