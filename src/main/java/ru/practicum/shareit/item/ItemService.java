package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestCreateDto;
import ru.practicum.shareit.item.dto.ItemForRequestUpdateDto;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ItemService {
    private final ItemDao itemDao;
    private final UserService userService;

    public Item findItem(long userId, long id) {
        userService.findUser(userId);

        return itemDao.find(id).orElseThrow(() -> new IdNotFoundException("Item with this id not exist"));
    }

    public List<Item> findAllItems(long userId) {
        userService.findUser(userId);

        return itemDao.findAll(userId);
    }


    public Item createNewItem(long userId, ItemForRequestCreateDto itemDto) {
        log.debug("+ createNewItem: {}, {}", userId, itemDto);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.findUser(userId));

        return itemDao.create(item);
    }

    public Item updateItem(long userId, long id, ItemForRequestUpdateDto itemDto) {
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
