package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.Violation;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemMapper itemMapper;
    @MockBean
    @Qualifier("itemServiceDbImpl")
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;

    private final User testUser1 = User.builder()
            .id(1L)
            .name("Иванов Иван")
            .email("IvanovIvan@gmail.com")
            .build();

    private final Item testItem1 = Item.builder()
            .id(1L)
            .name("Дрель ударная Bosh")
            .description("Мощность 7000W")
            .available(true)
            .owner(testUser1)
            .request(null)
            .build();

    private final Item testItem2 = Item.builder()
            .id(2L)
            .name("Дрель аккумуляторная")
            .description("В комплекте запасной аккумулятор и набор бит")
            .available(true)
            .owner(testUser1)
            .request(null)
            .build();

    private final Item testItem3 = Item.builder()
            .id(3L)
            .name("Байдарка трёхместная Ладога")
            .description("2003г.в. в отличном состоянии, весла отсутствуют")
            .available(true)
            .owner(testUser1)
            .request(null)
            .build();

    private ItemForResponseDto createItemForResponseDto(Item item) {
        return ItemForResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

    @BeforeEach
    void createItemMapperMock() {
        Mockito.when(itemMapper.itemToItemForResponseDto(Mockito.any()))
                .thenAnswer(invocation -> createItemForResponseDto(invocation.getArgument(0)));
    }

    @Test
    @DisplayName("GET /items/{id} returns HTTP-response with status code 200, content type application/json and " +
            "correct item")
    void shouldReturnItem() throws Exception {
        Mockito.when(itemService.findItem(1L, 3L)).thenReturn(testItem3);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/3")
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(createItemForResponseDto(testItem3))));
    }

    @Test
    @DisplayName("GET /items/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when user or item id is not exist")
    void shouldNotReturnItemWithIdNotExist() throws Exception {
        final long wrongId = 100L;
        final Violation itemErrorResponse = new Violation("id", "Item with this id not exist");
        final Violation userErrorResponse = new Violation("id", "User with this id not exist");
        Mockito.when(itemService.findItem(wrongId, 1L)).thenThrow(new IdNotFoundException("User with this id " +
                "not exist"));
        Mockito.when(itemService.findItem(1L, wrongId)).thenThrow(new IdNotFoundException("Item with this id " +
                "not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", wrongId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(userErrorResponse)));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/" + wrongId)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(itemErrorResponse)));
    }

    @Test
    @DisplayName("GET /items returns HTTP-response with status code 200, content type application/json and correct " +
            "list of items")
    void shouldReturnAllItems() throws Exception {
        final List<Item> testItems = List.of(testItem1, testItem2, testItem3);
        Mockito.when(itemService.findAllItems(1L)).thenReturn(testItems);

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(testItems.stream()
                        .map(this::createItemForResponseDto)
                        .collect(Collectors.toList()))));
    }

    @Test
    @DisplayName("GET /items returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when user id is not exist")
    void shouldNotReturnAllItemsWithIdNotExist() throws Exception {
        final long wrongId = 100L;
        final Violation errorResponse = new Violation("id", "User with this id not exist");
        Mockito.when(itemService.findAllItems(wrongId)).thenThrow(new IdNotFoundException("User with this id " +
                "not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", wrongId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("GET /items/search?text={text} returns HTTP-response with status code 200, content type " +
            "application/json and correct searched items")
    void shouldReturnSearchedItems() throws Exception {
        final String text = "Дрель";
        final List<Item> testItems = List.of(testItem1, testItem2);
        Mockito.when(itemService.searchItem(1L, text)).thenReturn(testItems);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text=" + text)
                        .header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(testItems.stream()
                        .map(this::createItemForResponseDto)
                        .collect(Collectors.toList()))));
    }

    @Test
    @DisplayName("GET /items/search?text={text} returns HTTP-response with status code 404, content type " +
            "application/json and error massage, when user id is not exist")
    void shouldNotReturnSearchedItemsWithIdNotExist() throws Exception {
        final String text = "Дрель";
        final long wrongId = 100L;
        final Violation errorResponse = new Violation("id", "User with this id not exist");
        Mockito.when(itemService.searchItem(wrongId, text)).thenThrow(new IdNotFoundException("User with this id " +
                "not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text=" + text)
                        .header("X-Sharer-User-Id", wrongId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("POST /items returns HTTP-response with status code 200, content type application/json and correct " +
            "created item")
    void shouldCreateNewItem() throws Exception {
        final ItemForRequestDto itemDto = ItemForRequestDto.builder()
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .build();
        Mockito.when(itemService.createNewItem(1L, itemDto)).thenReturn(testItem1);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(createItemForResponseDto(testItem1))));
    }

    @Test
    @DisplayName("POST /items returns HTTP-response with status code 400, content type application/json and " +
            "validation error massage, when input item`s fields is null")
    void shouldNotCreateItemWithNullFields() throws Exception {
        final ItemForRequestDto incorrectItemDto = ItemForRequestDto.builder()
                .name(null)
                .description(null)
                .available(null)
                .build();
        final List<Violation> errorResponse = List.of(new Violation("name", "must not be blank"),
                new Violation("description", "must not be blank"),
                new Violation("available", "must not be null"));

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incorrectItemDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("POST /items returns HTTP-response with status code 404, content type application/json and error " +
            "massage, when user id is not exist")
    void shouldNotCreateItemWithIdNotExist() throws Exception {
        final long wrongId = 100L;
        final ItemForRequestDto itemDto = ItemForRequestDto.builder()
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .build();
        final Violation errorResponse = new Violation("id", "User with this id not exist");
        Mockito.when(itemService.createNewItem(wrongId, itemDto))
                .thenThrow(new IdNotFoundException("User with this id not exist"));

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", wrongId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("PATCH /items/{id} returns HTTP-response with status code 200, content type application/json and " +
            "correct changed item")
    void shouldUpdateUser() throws Exception {
        final ItemForRequestDto itemDto = ItemForRequestDto.builder()
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .build();
        Mockito.when(itemService.updateItem(1L, testItem1.getId(), itemDto)).thenReturn(testItem1);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/" + testItem1.getId())
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(createItemForResponseDto(testItem1))));
    }

    @Test
    @DisplayName("PATCH /items/{id} returns HTTP-response with status code 403, content type application/json and " +
            "error message, when user does not access to item")
    void shouldNotUpdateItemWithoutAccess() throws Exception {
        final long wrongId = 2L;
        final ItemForRequestDto itemDto = ItemForRequestDto.builder()
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .build();
        final Violation errorResponse = new Violation("userId", "User does not have access to target " +
                "item");
        Mockito.when(itemService.updateItem(wrongId, testItem1.getId(), itemDto))
                .thenThrow(new AccessNotAllowedException("User does not have access to target item"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/" + testItem1.getId())
                        .header("X-Sharer-User-Id", wrongId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("PATCH /items/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error message, when user id is not exist")
    void shouldNotUpdateItemWithIdNotExist() throws Exception {
        final long wrongId = 100L;
        final ItemForRequestDto itemDto = ItemForRequestDto.builder()
                .name("Дрель ударная Bosh")
                .description("Мощность 7000W")
                .available(true)
                .build();
        final Violation errorResponse = new Violation("id", "User with this id not exist");
        Mockito.when(itemService.updateItem(wrongId, testItem1.getId(), itemDto))
                .thenThrow(new IdNotFoundException("User with this id not exist"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/" + testItem1.getId())
                        .header("X-Sharer-User-Id", wrongId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }
}
