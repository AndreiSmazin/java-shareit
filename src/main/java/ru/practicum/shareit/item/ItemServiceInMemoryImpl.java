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

    public ExtendedItemForResponseDto findItem(long userId, long id) {
        userService.findUser(userId);

        Item item = itemDao.find(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Item with id %s not exist", id)));

        return itemMapper.itemToExtendedItemForResponseDto(item);
    }

    public Item findItem(long id) {
        return itemDao.find(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Item with id %s not exist", id)));
    }

    public List<ExtendedItemForResponseDto> findAllItems(long userId) {
        userService.findUser(userId);

        List<Item> items = itemDao.findAll(userId);

        return items.stream()
                .map(itemMapper::itemToExtendedItemForResponseDto)
                .collect(Collectors.toList());
    }


    public ItemForResponseDto createNewItem(long userId, ItemForRequestDto itemDto) {
        log.debug("+ createNewItem: {}, {}", userId, itemDto);

        Item item = itemMapper.itemForRequestDtoToItem(itemDto);
        item.setOwner(userService.findUser(userId));

        return itemMapper.itemToItemForResponseDto(itemDao.create(item));
    }

    public ItemForResponseDto updateItem(long userId, long id, ItemForRequestDto itemDto) {
        log.debug("+ updateItem: {}, {}, {}", userId, id, itemDto);

        userService.findUser(userId);

        Item targetItem = findItem(id);
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

    public List<ItemForResponseDto> searchItem(long userId, String text) {
        userService.findUser(userId);

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
