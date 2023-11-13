package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseWithBookingsDto;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier("itemServiceDbImpl")
@Slf4j
public class ItemServiceDbImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    public ItemServiceDbImpl(ItemRepository itemRepository,
                             BookingRepository bookingRepository,
                             @Qualifier("userServiceDbImpl") UserService userService,
                             ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemForResponseWithBookingsDto findItem(long userId, long id) {
        userService.findUser(userId);

        Item item = itemRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Item with id %s not exist", id)));

        ItemForResponseWithBookingsDto itemDto = itemMapper.itemToItemForResponseWithBookingsDto(item);
        if (item.getOwner().getId() == userId) {
            addBookings(itemDto);
        }
        System.out.println(itemDto);
        return itemDto;
    }

    @Override
    public Item findItem(long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Item with id %s not exist", id)));
    }

    @Override
    public List<ItemForResponseWithBookingsDto> findAllItems(long userId) {
        long ownerId = userService.findUser(userId).getId();

        List<Item> items = itemRepository.findAllByOwnerIdOrderById(ownerId);

        return items.stream()
                .map(itemMapper::itemToItemForResponseWithBookingsDto)
                .peek(this::addBookings)
                .collect(Collectors.toList());
    }

    @Override
    public Item createNewItem(long userId, ItemForRequestDto itemDto) {
        log.debug("+ createNewItem: {}, {}", userId, itemDto);

        Item item = itemMapper.itemForRequestDtoToItem(itemDto);
        item.setOwner(userService.findUser(userId));

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(long userId, long id, ItemForRequestDto itemDto) {
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
        itemRepository.save(targetItem);

        return targetItem;
    }

    @Override
    public List<Item> searchItem(long userId, String text) {
        userService.findUser(userId);

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findItemsByNameOrDescription(text);
    }

    private void validateOwner(long userId, Item item) {
        if (userId != item.getOwner().getId()) {
            throw new AccessNotAllowedException(String.format("User %s does not have access to target item", userId));
        }
    }

    private void addBookings(ItemForResponseWithBookingsDto itemDto) {
        List<BookingForItemDto> pastBookings = bookingRepository.findPastBookings(itemDto.getId());
        itemDto.setLastBooking(pastBookings.isEmpty() ? null : pastBookings.get(0));
        System.out.println(itemDto.getLastBooking());

        List<BookingForItemDto> futureBookings = bookingRepository.findFutureBookings(itemDto.getId());
        itemDto.setNextBooking(pastBookings.isEmpty() ? null : futureBookings.get(0));
        System.out.println(itemDto.getNextBooking());
    }
}
