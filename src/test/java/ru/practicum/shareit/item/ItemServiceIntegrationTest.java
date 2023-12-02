package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.item.dto.CommentForRequestDto;
import ru.practicum.shareit.item.dto.CommentForResponseDto;
import ru.practicum.shareit.item.dto.ExtendedItemForResponseDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest
@AutoConfigureTestDatabase
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Test
    @DisplayName("Method findItem(long userId, long id) should return expected item")
    void shouldReturnItemById() throws Exception {
        final List<CommentForResponseDto> itemComments = List.of(
                CommentForResponseDto.builder()
                        .id(1)
                        .text("Брали для похода по Ладоге. Спасибо, не подвела!")
                        .authorName("Алена Васина")
                        .created(LocalDateTime.parse("2023-08-10T00:00:00"))
                        .build(),
                CommentForResponseDto.builder()
                        .id(2)
                        .text("На трехместной самое то вдвоём с грузом")
                        .authorName("Сергей Иванов")
                        .created(LocalDateTime.parse("2023-07-02T00:00:00"))
                        .build());
        final ExtendedItemForResponseDto expectedItem = ExtendedItemForResponseDto.builder()
                .id(3)
                .name("Байдарка трёхместная Ладога")
                .description("2003г.в. в отличном состоянии, весла отсутствуют")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(itemComments)
                .build();

        final ExtendedItemForResponseDto item = itemService.findItem(2L, 3L);

        assertEquals(expectedItem, item, "Item and expectedItem is not match");
    }

    @Test
    @DisplayName("Method findItem(long userId, long id) should return expected item for item owner")
    void shouldReturnItemByIdForOwner() throws Exception {
        final List<CommentForResponseDto> itemComments = List.of(
                CommentForResponseDto.builder()
                        .id(1)
                        .text("Брали для похода по Ладоге. Спасибо, не подвела!")
                        .authorName("Алена Васина")
                        .created(LocalDateTime.parse("2023-08-10T00:00:00"))
                        .build(),
                CommentForResponseDto.builder()
                        .id(2)
                        .text("На трехместной самое то вдвоём с грузом")
                        .authorName("Сергей Иванов")
                        .created(LocalDateTime.parse("2023-07-02T00:00:00"))
                        .build());
        final ExtendedItemForResponseDto expectedItem = ExtendedItemForResponseDto.builder()
                .id(3)
                .name("Байдарка трёхместная Ладога")
                .description("2003г.в. в отличном состоянии, весла отсутствуют")
                .available(true)
                .lastBooking(BookingForItemDto.builder()
                        .id(1L)
                        .bookerId(4L)
                        .build())
                .nextBooking(BookingForItemDto.builder()
                        .id(3L)
                        .bookerId(2L)
                        .build())
                .comments(itemComments)
                .build();

        final ExtendedItemForResponseDto item = itemService.findItem(1L, 3L);

        assertEquals(expectedItem, item, "Item and expectedItem is not match");
    }

    @Test
    @DisplayName("Method findItem(long userId, long id) should throw IdNotFoundException when Item or User is not" +
            " found")
    void shouldThrowExceptionWhenItemNotFound() throws Exception {
        final String itemExpectedMessage = "Item with id 100 not exist";
        final String userExpectedMessage = "User with id 100 not exist";

        final IdNotFoundException itemException = assertThrows(
                IdNotFoundException.class,
                () -> itemService.findItem(1L, 100L));
        final IdNotFoundException userException = assertThrows(
                IdNotFoundException.class,
                () -> itemService.findItem(100L, 1L));

        assertEquals(itemExpectedMessage, itemException.getMessage(), "Exception massage and expectedMassage" +
                " is not match");
        assertEquals(userExpectedMessage, userException.getMessage(), "Exception massage and expectedMassage" +
                " is not match");
    }

    @Test
    @DisplayName("Method updateItem(long userId, long id, ItemForRequestDto itemDto) should return expected updated" +
            " item")
    void shouldUpdateItem() throws Exception {
        final ItemForResponseDto expectedItem = ItemForResponseDto.builder()
                .id(6L)
                .name("Штатив для телескопа")
                .description("Складной в чехле, крепление резьбовое 8 мм")
                .available(true)
                .requestId(null)
                .build();
        final ItemForRequestDto itemDto = ItemForRequestDto.builder()
                .name("Штатив для телескопа")
                .description("Складной в чехле, крепление резьбовое 8 мм")
                .available(null)
                .requestId(null)
                .build();

        final ItemForResponseDto item = itemService.updateItem(2L, 6L, itemDto);

        assertEquals(expectedItem, item, "Item and expectedItem is not match");
    }

    @Test
    @DisplayName("Method findItem(long userId, long id) should throw AccessNotAllowedException when User does not" +
            " have access to target item")
    void shouldThrowExceptionWhenUserNotOwner() throws Exception {
        final String expectedMessage = "User 1 does not have access to target item";
        final ItemForRequestDto itemDto = ItemForRequestDto.builder()
                .name("Штатив для телескопа")
                .description("Складной в чехле, крепление резьбовое 8 мм")
                .available(null)
                .requestId(null)
                .build();

        final AccessNotAllowedException e = assertThrows(
                AccessNotAllowedException.class,
                () -> itemService.updateItem(1L, 6L, itemDto));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method createNewComment(long userId, long itemId, CommentForRequestDto commentDto) should return" +
            " expected comment")
    void shouldCreateNewComment() throws Exception {
        final CommentForRequestDto commentDto = CommentForRequestDto.builder()
                .text("Какая-то ерунда, крутится не в ту сторону. Не закручивает винты")
                .build();

        final CommentForResponseDto comment = itemService.createNewComment(2L, 2L, commentDto);
        final LocalDateTime currentTime = comment.getCreated();

        final CommentForResponseDto expectedComment = CommentForResponseDto.builder()
                .id(3)
                .text("Какая-то ерунда, крутится не в ту сторону. Не закручивает винты")
                .authorName("Ирина Боброва")
                .created(currentTime)
                .build();

        assertEquals(expectedComment, comment, "Comment and expectedComment is not match");
    }

    @Test
    @DisplayName("Method createNewComment(long userId, long itemId, CommentForRequestDto commentDto) should throw" +
            " RequestValidationException when User does not have completed bookings of item")
    void shouldThrowExceptionWhenUserNotBooker() throws Exception {
        final String expectedMessage = "User 4 does not have completed bookings of item 2";
        final CommentForRequestDto commentDto = CommentForRequestDto.builder()
                .text("Какая-то ерунда, крутится не в ту сторону. Не закручивает винты")
                .build();

        final RequestValidationException e = assertThrows(
                RequestValidationException.class,
                () -> itemService.createNewComment(4L, 2L, commentDto));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method findAllItems(long userId, int from, int size) should return correct list of Items")
    void shouldReturnItems() throws Exception {
        final ExtendedItemForResponseDto expectedItem1 = ExtendedItemForResponseDto.builder()
                .id(1)
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
        final List<CommentForResponseDto> expectedItem2Comments = List.of(
                CommentForResponseDto.builder()
                        .id(1)
                        .text("Брали для похода по Ладоге. Спасибо, не подвела!")
                        .authorName("Алена Васина")
                        .created(LocalDateTime.parse("2023-08-10T00:00:00"))
                        .build(),
                CommentForResponseDto.builder()
                        .id(2)
                        .text("На трехместной самое то вдвоём с грузом")
                        .authorName("Сергей Иванов")
                        .created(LocalDateTime.parse("2023-07-02T00:00:00"))
                        .build());
        final ExtendedItemForResponseDto expectedItem2 = ExtendedItemForResponseDto.builder()
                .id(3)
                .name("Байдарка трёхместная Ладога")
                .description("2003г.в. в отличном состоянии, весла отсутствуют")
                .available(true)
                .lastBooking(BookingForItemDto.builder()
                        .id(1L)
                        .bookerId(4L)
                        .build())
                .nextBooking(BookingForItemDto.builder()
                        .id(3L)
                        .bookerId(2L)
                        .build())
                .comments(expectedItem2Comments)
                .build();
        final ExtendedItemForResponseDto expectedItem3 = ExtendedItemForResponseDto.builder()
                .id(4)
                .name("Набор походных котелков")
                .description("3 штуки: 3, 4, 5 литров")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
        final List<ExtendedItemForResponseDto> expectedItems = List.of(expectedItem1, expectedItem2, expectedItem3);

        final List<ExtendedItemForResponseDto> items = itemService.findAllItems(1L, 0, 20);

        assertEquals(expectedItems, items, "Items and expectedItems is not match");
    }

    @Test
    @DisplayName("Method searchItem(long userId, String text, int from, int size) should return correct list of Items")
    void shouldSearchItems() throws Exception {
        final ItemForResponseDto expectedItem1 = ItemForResponseDto.builder()
                .id(1)
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(null)
                .build();
        final ItemForResponseDto expectedItem2 = ItemForResponseDto.builder()
                .id(2)
                .name("Дрель аккумуляторная")
                .description("В комплекте запасной аккумулятор и набор бит")
                .available(true)
                .requestId(null)
                .build();
        final List<ItemForResponseDto> expectedItems = List.of(expectedItem1, expectedItem2);

        final List<ItemForResponseDto> items = itemService.searchItem(2L, "дрель", 0, 20);

        assertEquals(expectedItems, items, "Items and expectedItems is not match");
    }

    @Test
    @DisplayName("Method searchItem(long userId, String text, int from, int size) should return empty list of Items" +
            " when Items not searched")
    void shouldReturnEmptyListWhenItemsNotSearched() throws Exception {
        final List<ItemForResponseDto> items = itemService.searchItem(2L, "фофудья", 0, 20);

        assertTrue(items.isEmpty(), "Items list is not empty");
    }
}
