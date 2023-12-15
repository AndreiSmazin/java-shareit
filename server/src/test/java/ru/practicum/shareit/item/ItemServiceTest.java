package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateUpdateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceDbImpl;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    private ItemMapper itemMapper = new ItemMapperImpl();
    @Mock
    private ItemRequestService itemRequestService;
    private ItemService itemService;

    @BeforeEach
    void initiateItemService() {
        itemService = new ItemServiceDbImpl(itemRepository,
                bookingRepository,
                commentRepository,
                userService,
                itemMapper,
                itemRequestService);
    }

    @Test
    @DisplayName("Method checkItem(long id) should return correct Item")
    void shouldReturnItem() throws Exception {
        final User testUser = User.builder()
                .id(3L)
                .name("Сергей Иванов")
                .email("SupremeSerg91@yandex.com")
                .build();
        final Item expectedItem = Item.builder()
                .id(12L)
                .name("Байдарка трёхместная Ладога")
                .description("2003г.в. в отличном состоянии, весла отсутствуют")
                .available(true)
                .owner(testUser)
                .requestId(4L)
                .build();

        Mockito.when(itemRepository.findById(12L)).thenReturn(Optional.of(expectedItem));

        final Item item = itemService.checkItem(12L);

        Assertions.assertEquals(expectedItem, item, "Item and expectedItem is not match");
    }

    @Test
    @DisplayName("Method checkItem(long id) should throw IdNotFoundException when Item is not found")
    void shouldThrowExceptionWhenItemNotFound() throws Exception {
        final String expectedMessage = "Item with id 100 not exist";

        Mockito.when(itemRepository.findById(100L)).thenReturn(Optional.empty());

        final Exception e = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemService.checkItem(100L));

        Assertions.assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method createNewItem(long userId, ItemForRequestDto itemDto) should return correct created Item")
    void shouldReturnCreatedItem() throws Exception {
        final User testUser = User.builder()
                .id(3L)
                .name("Сергей Иванов")
                .email("SupremeSerg91@yandex.com")
                .build();
        final ItemResponseDto expectedItem = ItemResponseDto.builder()
                .id(1)
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(1L)
                .build();
        final ItemCreateUpdateDto itemDto = ItemCreateUpdateDto.builder()
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(1L)
                .build();
        final Item newItem = Item.builder()
                .id(1)
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .owner(testUser)
                .requestId(1L)
                .build();

        Mockito.when(itemRequestService.checkItemRequest(1L)).thenReturn(new ItemRequest());
        Mockito.when(userService.checkUser(3L)).thenReturn(testUser);

        Item item = itemMapper.itemForRequestDtoToItem(itemDto);
        item.setOwner(testUser);
        Mockito.when(itemRepository.save(item)).thenReturn(newItem);

        final ItemResponseDto createdItem = itemService.createNewItem(3L, itemDto);

        Assertions.assertEquals(expectedItem, createdItem, "CreatedItem and expectedItem is not match");
    }

    @Test
    @DisplayName("Method searchItem(long userId, String text, int from, int size) should return empty list of Items" +
            " when text is blank")
    void shouldReturnEmptyListWhenSearchTextBlank() throws Exception {
        Mockito.when(userService.checkUser(3L)).thenReturn(new User());

        final List<ItemResponseDto> items = itemService.searchItem(3L, "", 0, 20);

        Assertions.assertTrue(items.isEmpty(), "Items list is not empty");
    }

    @Test
    @DisplayName("Methods findItem, findAllItems, createNewItem, updateItem, searchItem and createNewComment should" +
            " throw IdNotFoundException when User is not found")
    void shouldThrowExceptionWhenUserNotExists() throws Exception {
        final String expectedMessage = "User with id 100 not exist";

        Mockito.when(userService.checkUser(100L)).thenThrow(new IdNotFoundException("User with id 100 not exist"));

        final Exception findItemException = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemService.findItem(100L, 1L));

        Assertions.assertEquals(expectedMessage, findItemException.getMessage(), "Exception massage and" +
                " expectedMassage is not match");

        final Exception findAllItemsException = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemService.findAllItems(100L, 0, 20));

        Assertions.assertEquals(expectedMessage, findAllItemsException.getMessage(), "Exception massage and" +
                " expectedMassage is not match");

        final ItemCreateUpdateDto itemDto = ItemCreateUpdateDto.builder()
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(null)
                .build();
        final Exception createNewItemException = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemService.createNewItem(100L, itemDto));

        Assertions.assertEquals(expectedMessage, createNewItemException.getMessage(), "Exception massage and" +
                " expectedMassage is not match");

        final Exception updateItemException = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemService.updateItem(100L, 1L, itemDto));

        Assertions.assertEquals(expectedMessage, updateItemException.getMessage(), "Exception massage and" +
                " expectedMassage is not match");

        final Exception searchItemException = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemService.searchItem(100L, "Кошка", 0, 20));

        Assertions.assertEquals(expectedMessage, searchItemException.getMessage(), "Exception massage and" +
                " expectedMassage is not match");

        final CommentCreateDto commentDto = CommentCreateDto.builder()
                .text("Какая-то ерунда, крутится не в ту сторону. Не закручивает винты")
                .build();
        final Exception createNewCommentException = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemService.createNewComment(100L, 1L, commentDto));

        Assertions.assertEquals(expectedMessage, createNewCommentException.getMessage(), "Exception massage and" +
                " expectedMassage is not match");
    }
}
