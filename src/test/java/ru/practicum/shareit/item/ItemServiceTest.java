package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldReturnUser() throws Exception {
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

        assertEquals(expectedItem, item, "Item and expectedItem is not match");
    }

    @Test
    @DisplayName("Method checkItem(long id) should throw IdNotFoundException when Item is not found")
    void shouldThrowExceptionWhenUserNotFound() throws Exception {
        final String expectedMessage = "Item with id 100 not exist";

        Mockito.when(itemRepository.findById(100L)).thenReturn(Optional.empty());

        final Exception e = assertThrows(IdNotFoundException.class, () ->
                itemService.checkItem(100L));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method createNewItem(long userId, ItemForRequestDto itemDto) should return correct created Item")
    void shouldReturnCreatedItem() throws Exception {
        final User testUser = User.builder()
                .id(3L)
                .name("Сергей Иванов")
                .email("SupremeSerg91@yandex.com")
                .build();
        final ItemForResponseDto expectedItem = ItemForResponseDto.builder()
                .id(1)
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(1L)
                .build();
        final ItemForRequestDto itemDto = ItemForRequestDto.builder()
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

        final ItemForResponseDto createdItem = itemService.createNewItem(3L, itemDto);

        assertEquals(expectedItem, createdItem, "CreatedItem and expectedItem is not match");
    }

    @Test
    @DisplayName("Method searchItem(long userId, String text, int from, int size) should return empty list of Items" +
            " when text is blank")
    void shouldReturnEmptyListWhenSearchTextBlank() throws Exception {
        Mockito.when(userService.checkUser(3L)).thenReturn(new User());

        final List<ItemForResponseDto> items = itemService.searchItem(3L, "", 0, 20);

        assertTrue(items.isEmpty(), "Items list is not empty");
    }
}
