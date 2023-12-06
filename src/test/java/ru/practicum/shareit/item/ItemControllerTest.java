package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.ExceptionViolation;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ValidationViolation;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    @DisplayName("GET /items/{id} returns HTTP-response with status code 200, content type application/json and " +
            "correct item")
    void shouldReturnItem() throws Exception {
        final ItemExtendedResponseDto testItem = ItemExtendedResponseDto.builder()
                .id(1L)
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        Mockito.when(itemService.findItem(1L, 3L)).thenReturn(testItem);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/3")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(testItem)));
    }

    @Test
    @DisplayName("GET /items/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when user or item id is not exist")
    void shouldNotReturnItemWithIdNotExist() throws Exception {
        final ExceptionViolation itemErrorResponse = new ExceptionViolation("Item with id 100 not exist");
        final ExceptionViolation userErrorResponse = new ExceptionViolation("User with id 100 not exist");

        Mockito.when(itemService.findItem(100L, 1L)).thenThrow(new IdNotFoundException("User with id 100" +
                " not exist"));
        Mockito.when(itemService.findItem(1L, 100L)).thenThrow(new IdNotFoundException("Item with id 100" +
                " not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", 100))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userErrorResponse)));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/100")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemErrorResponse)));
    }

    @Test
    @DisplayName("GET /items returns HTTP-response with status code 200, content type application/json and correct " +
            "list of items")
    void shouldReturnAllItems() throws Exception {
        final ItemExtendedResponseDto testItem1 = ItemExtendedResponseDto.builder()
                .id(1L)
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();
        final ItemExtendedResponseDto testItem2 = ItemExtendedResponseDto.builder()
                .id(2L)
                .name("Байдарка трёхместная Ладога")
                .description("2003г.в. в отличном состоянии, весла отсутствуют")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();
        final List<ItemExtendedResponseDto> testItems = List.of(testItem1, testItem2);

        Mockito.when(itemService.findAllItems(1L, 0, 30)).thenReturn(testItems);

        mockMvc.perform(MockMvcRequestBuilders.get("/items?from=0&size=30")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testItems)));
    }

    @Test
    @DisplayName("GET /items returns HTTP-response with status code 400, content type application/json and " +
            "error massage, when request params is wrong")
    void shouldNotReturnAllItemsWithWrongRequestParams() throws Exception {
        final List<ValidationViolation> errorResponse = List.of(
                new ValidationViolation("from", "must be greater than or equal to 0"),
                new ValidationViolation("size", "must be less than or equal to 100"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items?from=-1&size=250")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("GET /items/search?text={text} returns HTTP-response with status code 200, content type " +
            "application/json and correct searched items")
    void shouldReturnSearchedItems() throws Exception {
        final ItemResponseDto testItem1 = ItemResponseDto.builder()
                .id(1L)
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(null)
                .build();
        final ItemResponseDto testItem2 = ItemResponseDto.builder()
                .id(3L)
                .name("Дрель аккумуляторная")
                .description("В комплекте запасной аккумулятор и набор бит")
                .available(true)
                .requestId(null)
                .build();
        final List<ItemResponseDto> testItems = List.of(testItem1, testItem2);

        Mockito.when(itemService.searchItem(1L, "Дрель", 0, 20)).thenReturn(testItems);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text=Дрель")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testItems)));
    }

    @Test
    @DisplayName("POST /items returns HTTP-response with status code 200, content type application/json and correct " +
            "created item")
    void shouldCreateNewItem() throws Exception {
        final ItemCreateUpdateDto itemDto = ItemCreateUpdateDto.builder()
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(1L)
                .build();
        final ItemResponseDto testItem = ItemResponseDto.builder()
                .id(1L)
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(1L)
                .build();

        Mockito.when(itemService.createNewItem(1L, itemDto)).thenReturn(testItem);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(testItem)));
    }

    @Test
    @DisplayName("POST /items returns HTTP-response with status code 400, content type application/json and " +
            "validation error massage, when input item`s fields is null")
    void shouldNotCreateItemWithNullFields() throws Exception {
        final ItemCreateUpdateDto incorrectItemDto = ItemCreateUpdateDto.builder()
                .name(null)
                .description(null)
                .available(null)
                .requestId(1L)
                .build();
        final List<ValidationViolation> errorResponse = List.of(
                new ValidationViolation("name", "must not be blank"),
                new ValidationViolation("description", "must not be blank"),
                new ValidationViolation("available", "must not be null"));

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incorrectItemDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("PATCH /items/{id} returns HTTP-response with status code 200, content type application/json and " +
            "correct changed item")
    void shouldUpdateUser() throws Exception {
        final ItemCreateUpdateDto itemDto = ItemCreateUpdateDto.builder()
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(null)
                .build();
        final ItemResponseDto testItem = ItemResponseDto.builder()
                .id(1L)
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(null)
                .build();

        Mockito.when(itemService.updateItem(1L, testItem.getId(), itemDto)).thenReturn(testItem);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(testItem)));
    }

    @Test
    @DisplayName("PATCH /items/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error message, when user does not access to item")
    void shouldNotUpdateItemWithoutAccess() throws Exception {
        final ItemCreateUpdateDto itemDto = ItemCreateUpdateDto.builder()
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .requestId(null)
                .build();
        final ExceptionViolation errorResponse = new ExceptionViolation("User 2 does not have access to target " +
                "item");
        Mockito.when(itemService.updateItem(2L, 1L, itemDto))
                .thenThrow(new AccessNotAllowedException("User 2 does not have access to target item"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("POST /items/{id}/comment returns HTTP-response with status code 200, content type application/json" +
            " and correct created comment")
    void shouldCreateNewComment() throws Exception {
        final CommentCreateDto commentDto = CommentCreateDto.builder()
                .text("Отличная дрель, без проблем просверлила монолитный бетон")
                .build();
        final CommentResponseDto testComment = CommentResponseDto.builder()
                .id(1L)
                .text("Отличная дрель, без проблем просверлила монолитный бетон")
                .authorName("Василий Михайлов")
                .created(LocalDateTime.parse("2023-11-26T20:00:00"))
                .build();

        Mockito.when(itemService.createNewComment(1L, 1L, commentDto)).thenReturn(testComment);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testComment)));
    }

    @Test
    @DisplayName("POST /items/{id}/comment returns HTTP-response with status code 400, content type application/json" +
            " and validation error massage, when input comment`s text field is null")
    void shouldNotCreateNewCommentWithNullText() throws Exception {
        final CommentCreateDto commentDto = CommentCreateDto.builder()
                .text(null)
                .build();
        final List<ValidationViolation> errorResponse = List.of(
                new ValidationViolation("text", "must not be blank"));

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }
}
