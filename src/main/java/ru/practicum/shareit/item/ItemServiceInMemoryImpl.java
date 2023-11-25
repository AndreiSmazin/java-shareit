package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceInMemoryImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ExtendedItemForResponseDto findItem(long userId, long id) {
        userService.findUser(userId);

        Item item = itemDao.find(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Item with id %s not exist", id)));

        return itemMapper.itemToExtendedItemForResponseDto(item);
    }

    @Override
    public Item checkItem(long id) {
        return itemDao.find(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Item with id %s not exist", id)));
    }

    @Override
    public List<ExtendedItemForResponseDto> findAllItems(long userId, int from, int size) {
        userService.checkUser(userId);

        List<Item> items = itemDao.findAll(userId);

        return items.stream()
                .map(itemMapper::itemToExtendedItemForResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemForResponseDto createNewItem(long userId, ItemForRequestDto itemDto) {
        log.debug("+ createNewItem: {}, {}", userId, itemDto);

        Item item = itemMapper.itemForRequestDtoToItem(itemDto);
        item.setOwner(userService.checkUser(userId));

        return itemMapper.itemToItemForResponseDto(itemDao.create(item));
    }

    @Override
    public ItemForResponseDto updateItem(long userId, long id, ItemForRequestDto itemDto) {
        log.debug("+ updateItem: {}, {}, {}", userId, id, itemDto);

        userService.checkUser(userId);

        Item targetItem = checkItem(id);
        validateOwner(userId, targetItem);
        if (itemDto.getName() != null) {
            targetItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            targetItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            targetItem.setAvailable(itemDto.getAvailable());
        }
        itemDao.update(targetItem);

        return itemMapper.itemToItemForResponseDto(targetItem);
    }

    @Override
    public List<ItemForResponseDto> searchItem(long userId, String text, int from, int size) {
        userService.checkUser(userId);

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemDao.search(text).stream()
                .map(itemMapper::itemToItemForResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentForResponseDto createNewComment(long userId, long itemId, CommentForRequestDto commentDto) {
        return null;
    }

    private void validateOwner(long userId, Item item) {
        if (userId != item.getOwner().getId()) {
            throw new AccessNotAllowedException("User does not have access to target item");
        }
    }
}
