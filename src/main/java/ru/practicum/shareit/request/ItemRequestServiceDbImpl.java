package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.request.dto.ExtendedItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.user.UserService;

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
    public ExtendedItemRequestForResponseDto findItemRequestById(long userId, long id) {
        userService.checkUser(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("ItemRequest with id %s not exist", id)));

        ExtendedItemRequestForResponseDto itemRequestDto = itemRequestMapper
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
    public List<ExtendedItemRequestForResponseDto> findItemRequestsByUserId(long userId) {
        userService.checkUser(userId);

        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(itemRequestMapper::itemRequestToExtendedItemRequestForResponseDto)
                .peek(this::addItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExtendedItemRequestForResponseDto> findAllItemRequests(long userId, int from, int size) {
        userService.checkUser(userId);

        return itemRequestRepository.findAllFromOtherUsers(userId, PageRequest.of(from, size)).stream()
                .map(itemRequestMapper::itemRequestToExtendedItemRequestForResponseDto)
                .peek(this::addItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestForResponseDto createNewItemRequest(long userId, ItemRequestDto itemRequestDto) {
        log.debug("+ createNewItemRequest: {}, {}", userId, itemRequestDto);

        ItemRequest itemRequest = itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto);
        itemRequest.setRequester(userService.checkUser(userId));
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequestMapper.itemRequestToItemRequestForResponseDto(itemRequestRepository.save(itemRequest));
    }

    private void addItems(ExtendedItemRequestForResponseDto itemRequest) {
        List<ItemForResponseDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                .map(itemMapper::itemToItemForResponseDto)
                .collect(Collectors.toList());

        itemRequest.setItems(items);
    }
}
