package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ItemServiceInMemoryImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemServiceInMemoryImpl(ItemDao itemDao,
                                   @Qualifier("userServiceInMemoryImpl") UserService userService,
                                   ItemMapper itemMapper) {
        this.itemDao = itemDao;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    public Item findItem(long userId, long id) {
        userService.findUser(userId);

        return itemDao.find(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Item with id %s not exist", id)));
    }

    public List<Item> findAllItems(long userId) {
        userService.findUser(userId);

        return itemDao.findAll(userId);
    }


    public Item createNewItem(long userId, ItemForRequestDto itemDto) {
        log.debug("+ createNewItem: {}, {}", userId, itemDto);

        Item item = itemMapper.itemRequestDtoToItem(itemDto);
        item.setOwner(userService.findUser(userId));

        return itemDao.create(item);
    }

    public Item updateItem(long userId, long id, ItemForRequestDto itemDto) {
        log.debug("+ updateItem: {}, {}, {}", userId, id, itemDto);

        Item targetItem = findItem(userId, id);
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

        return targetItem;
    }

    public List<Item> searchItem(long userId, String text) {
        userService.findUser(userId);

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemDao.search(text);
    }

    private void validateOwner(long userId, Item item) {
        if (userId != item.getOwner().getId()) {
            throw new AccessNotAllowedException("User does not have access to target item");
        }
    }
}
