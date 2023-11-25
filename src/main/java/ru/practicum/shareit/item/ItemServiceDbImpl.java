package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.item.dto.CommentForRequestDto;
import ru.practicum.shareit.item.dto.CommentForResponseDto;
import ru.practicum.shareit.item.dto.ExtendedItemForResponseDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceDbImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemRequestService itemRequestService;

    @Override
    public ExtendedItemForResponseDto findItem(long userId, long id) {
        userService.checkUser(userId);

        Item item = itemRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Item with id %s not exist", id)));

        ExtendedItemForResponseDto itemDto = itemMapper.itemToExtendedItemForResponseDto(item);
        if (item.getOwner().getId() == userId) {
            addBookings(itemDto);
        }
        addComments(itemDto);

        return itemDto;
    }

    @Override
    public Item checkItem(long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Item with id %s not exist", id)));
    }

    @Override
    public List<ExtendedItemForResponseDto> findAllItems(long userId) {
        long ownerId = userService.checkUser(userId).getId();

        return itemRepository.findAllByOwnerIdOrderById(ownerId).stream()
                .map(itemMapper::itemToExtendedItemForResponseDto)
                .peek(this::addBookings)
                .peek(this::addComments)
                .collect(Collectors.toList());
    }

    @Override
    public ItemForResponseDto createNewItem(long userId, ItemForRequestDto itemDto) {
        log.debug("+ createNewItem: {}, {}", userId, itemDto);

        if (itemDto.getRequestId() != null) {
            itemRequestService.checkItemRequest(itemDto.getRequestId());
        }

        Item item = itemMapper.itemForRequestDtoToItem(itemDto);
        item.setOwner(userService.checkUser(userId));

        return itemMapper.itemToItemForResponseDto(itemRepository.save(item));
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
        if (itemDto.getRequestId() != null) {
            itemRequestService.checkItemRequest(itemDto.getRequestId());
            targetItem.setRequestId(itemDto.getRequestId());
        }

        return itemMapper.itemToItemForResponseDto(itemRepository.save(targetItem));
    }

    @Override
    public List<ItemForResponseDto> searchItem(long userId, String text) {
        userService.checkUser(userId);

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findItemsByNameOrDescription(text).stream()
                .map(itemMapper::itemToItemForResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentForResponseDto createNewComment(long userId, long itemId, CommentForRequestDto commentDto) {
        log.debug("+ createNewComment: {}, {}, {}", userId, itemId, commentDto);

        Comment comment = itemMapper.commentForRequestDtoToComment(commentDto);

        User author = userService.checkUser(userId);
        validateAuthor(userId);
        comment.setAuthor(author);
        comment.setItem(checkItem(itemId));
        comment.setCreated(LocalDateTime.now());

        return itemMapper.commentToCommentForResponseDto(commentRepository.save(comment), comment.getAuthor());
    }

    private void validateOwner(long userId, Item item) {
        if (userId != item.getOwner().getId()) {
            throw new AccessNotAllowedException(String.format("User %s does not have access to target item", userId));
        }
    }

    private void validateAuthor(long userId) {
        if (bookingRepository.findCountBookingsOfUser(userId) == 0) {
            throw new RequestValidationException(String.format("User %s does not have completed bookings", userId));
        }
    }

    private void addBookings(ExtendedItemForResponseDto itemDto) {
        List<BookingForItemDto> pastBookings = bookingRepository.findPastBookingsOfItem(itemDto.getId());
        itemDto.setLastBooking(pastBookings.isEmpty() ? null : pastBookings.get(0));

        List<BookingForItemDto> futureBookings = bookingRepository.findFutureBookingsOfItem(itemDto.getId());
        itemDto.setNextBooking(futureBookings.isEmpty() ? null : futureBookings.get(0));
    }

    private void addComments(ExtendedItemForResponseDto itemDto) {
        List<CommentForResponseDto> comments = commentRepository.findAllByItemId(itemDto.getId()).stream()
                .map(comment -> itemMapper.commentToCommentForResponseDto(comment, comment.getAuthor()))
                .collect(Collectors.toList());
        itemDto.setComments(comments);
    }
}
