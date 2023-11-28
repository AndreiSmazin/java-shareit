package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class ItemServiceIntegrationTest {
    private ItemService itemService;

    @Autowired
    public ItemServiceIntegrationTest(ItemServiceDbImpl itemService) {
        this.itemService = itemService;
    }

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
    @DisplayName("Method findItem(long userId, long id) should throw IdNotFoundException when User is not found")
    void shouldThrowExceptionWhenItemNotFound() throws Exception {
        final String expectedMessage = "Item with id 100 not exist";

        final IdNotFoundException e = assertThrows(
                IdNotFoundException.class,
                () -> itemService.findItem(1L, 100L));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
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
}
