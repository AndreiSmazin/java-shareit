package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.ExceptionViolation;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ValidationViolation;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    @DisplayName("GET /requests returns HTTP-response with status code 200, content type application/json and " +
            "correct list of requests")
    void shouldReturnRequestsOfUser() throws Exception {
        final ItemRequestExtendedResponseDto testItemRequest1 = ItemRequestExtendedResponseDto.builder()
                .id(1L)
                .description("Нужна бетономешалка на стройку загородного дома")
                .created(LocalDateTime.parse("2023-11-26T20:00:00"))
                .items(new ArrayList<>())
                .build();
        final ItemRequestExtendedResponseDto testItemRequest2 = ItemRequestExtendedResponseDto.builder()
                .id(2L)
                .description("Возьму в аренду прицеп для автомобиля с тентом")
                .created(LocalDateTime.parse("2023-11-30T20:00:00"))
                .items(new ArrayList<>())
                .build();
        final List<ItemRequestExtendedResponseDto> testItemRequests = List.of(testItemRequest1, testItemRequest2);

        Mockito.when(itemRequestService.findItemRequestsByUserId(1L)).thenReturn(testItemRequests);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testItemRequests)));
    }

    @Test
    @DisplayName("GET /requests returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when user id is not exist")
    void shouldNotReturnRequestsOfUserWithIdNotExist() throws Exception {
        final ExceptionViolation errorResponse = new ExceptionViolation("User with id 100 not exist");

        Mockito.when(itemRequestService.findItemRequestsByUserId(100L)).thenThrow(new IdNotFoundException("User with" +
                " id 100 not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 100))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("GET /requests/all returns HTTP-response with status code 200, content type application/json and " +
            "correct list of requests")
    void shouldReturnAllRequests() throws Exception {
        final ItemRequestExtendedResponseDto testItemRequest1 = ItemRequestExtendedResponseDto.builder()
                .id(1L)
                .description("Нужна бетономешалка на стройку загородного дома")
                .created(LocalDateTime.parse("2023-11-26T20:00:00"))
                .items(new ArrayList<>())
                .build();
        final ItemRequestExtendedResponseDto testItemRequest2 = ItemRequestExtendedResponseDto.builder()
                .id(2L)
                .description("Возьму в аренду прицеп для автомобиля с тентом")
                .created(LocalDateTime.parse("2023-11-30T20:00:00"))
                .items(new ArrayList<>())
                .build();
        final List<ItemRequestExtendedResponseDto> testItemRequests = List.of(testItemRequest1, testItemRequest2);

        Mockito.when(itemRequestService.findAllItemRequests(1L, 0, 20)).thenReturn(testItemRequests);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testItemRequests)));
    }

    @Test
    @DisplayName("GET /requests/{id} returns HTTP-response with status code 200, content type application/json and " +
            "correct request")
    void shouldReturnRequest() throws Exception {
        final ItemRequestExtendedResponseDto testItemRequest = ItemRequestExtendedResponseDto.builder()
                .id(1L)
                .description("Нужна бетономешалка на стройку загородного дома")
                .created(LocalDateTime.parse("2023-11-26T20:00:00"))
                .items(new ArrayList<>())
                .build();

        Mockito.when(itemRequestService.findItemRequestById(1L, 1L)).thenReturn(testItemRequest);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testItemRequest)));
    }

    @Test
    @DisplayName("GET /requests/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when request id is not exist")
    void shouldNotReturnRequestWithIdNotExist() throws Exception {
        final ExceptionViolation errorResponse = new ExceptionViolation("ItemRequest with id 100 not exist");

        Mockito.when(itemRequestService.findItemRequestById(1L, 100L)).thenThrow(
                new IdNotFoundException("ItemRequest with id 100 not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/100")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("POST /requests returns HTTP-response with status code 200, content type application/json and " +
            "correct created request")
    void shouldCreateRequest() throws Exception {
        final ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("Нужна бетономешалка на стройку загородного дома")
                .build();
        final ItemRequestResponseDto testItemRequest = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Нужна бетономешалка на стройку загородного дома")
                .created(LocalDateTime.parse("2023-11-26T20:00:00"))
                .build();

        Mockito.when(itemRequestService.createNewItemRequest(1L, itemRequestCreateDto)).thenReturn(testItemRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testItemRequest)));
    }

    @Test
    @DisplayName("POST /requests returns HTTP-response with status code 400, content type application/json and " +
            "error massage, when description is blank")
    void shouldNotCreateRequestWithDescriptionIsBlank() throws Exception {
        final ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description(" ")
                .build();
        final List<ValidationViolation> errorResponse = List.of(
                new ValidationViolation("description", "must not be blank"));

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }
}
