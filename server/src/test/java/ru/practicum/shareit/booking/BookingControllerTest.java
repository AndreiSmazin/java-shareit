package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.ExceptionViolation;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    @DisplayName("GET /bookings/{id} returns HTTP-response with status code 200, content type application/json and " +
            "correct booking")
    void shouldReturnBooking() throws Exception {
        final UserBookingDto testBooker = UserBookingDto.builder()
                .id(1L)
                .build();
        final ItemBookingDto testItem = ItemBookingDto.builder()
                .id(1L)
                .name("Петров Алексей")
                .build();
        final BookingResponseDto testBooking = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-11-26T20:00:00"))
                .end(LocalDateTime.parse("2023-11-30T20:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testBooker)
                .item(testItem)
                .build();

        Mockito.when(bookingService.findBooking(3L, 1L)).thenReturn(testBooking);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header("X-Sharer-User-Id", 3))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(testBooking)));
    }

    @Test
    @DisplayName("GET /bookings/{id} returns HTTP-response with status code 404, content type application/json and" +
            " error massage, when user or item id is not exist")
    void shouldNotReturnBookingWithIdNotExist() throws Exception {
        final ExceptionViolation bookingErrorResponse = new ExceptionViolation("Booking with id 100 not exist");
        final ExceptionViolation userErrorResponse = new ExceptionViolation("User with id 100 not exist");

        Mockito.when(bookingService.findBooking(100L, 1L)).thenThrow(new IdNotFoundException("User with id" +
                " 100 not exist"));
        Mockito.when(bookingService.findBooking(1L, 100L)).thenThrow(new IdNotFoundException("Booking with" +
                " id 100 not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header("X-Sharer-User-Id", 100))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userErrorResponse)));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/100")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingErrorResponse)));
    }

    @Test
    @DisplayName("GET /bookings/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error message, when user does not access to booking")
    void shouldNotReturnBookingWithoutAccess() throws Exception {
        final ExceptionViolation userErrorResponse = new ExceptionViolation("User 2 does not have access to" +
                " target booking");

        Mockito.when(bookingService.findBooking(2L, 1L))
                .thenThrow(new AccessNotAllowedException("User 2 does not have access to target booking"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userErrorResponse)));
    }

    @Test
    @DisplayName("GET /bookings returns HTTP-response with status code 200, content type application/json and " +
            "correct list of bookings")
    void shouldReturnAllBookingsOfUser() throws Exception {
        final UserBookingDto testBooker = UserBookingDto.builder()
                .id(1L)
                .build();
        final ItemBookingDto testItem1 = ItemBookingDto.builder()
                .id(1L)
                .name("Семенова Анна")
                .build();
        final BookingResponseDto testBooking1 = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-11-26T20:00:00"))
                .end(LocalDateTime.parse("2023-11-30T20:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testBooker)
                .item(testItem1)
                .build();
        final ItemBookingDto testItem2 = ItemBookingDto.builder()
                .id(2L)
                .name("Петров Алексей")
                .build();
        final BookingResponseDto testBooking2 = BookingResponseDto.builder()
                .id(2L)
                .start(LocalDateTime.parse("2023-11-30T20:00:00"))
                .end(LocalDateTime.parse("2023-12-02T20:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testBooker)
                .item(testItem2)
                .build();
        final List<BookingResponseDto> testBookings = List.of(testBooking1, testBooking2);

        Mockito.when(bookingService.findAllBookingsByUserId(1L, "ALL", 0, 20))
                .thenReturn(testBookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(testBookings)));
    }

    @Test
    @DisplayName("GET /bookings returns HTTP-response with status code 200, content type application/json and " +
            "correct list of bookings")
    void shouldReturnAllBookingsOfOwner() throws Exception {
        final UserBookingDto testBooker = UserBookingDto.builder()
                .id(1L)
                .build();
        final ItemBookingDto testItem1 = ItemBookingDto.builder()
                .id(1L)
                .name("Семенова Анна")
                .build();
        final BookingResponseDto testBooking1 = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-11-26T20:00:00"))
                .end(LocalDateTime.parse("2023-11-30T20:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testBooker)
                .item(testItem1)
                .build();
        final ItemBookingDto testItem2 = ItemBookingDto.builder()
                .id(2L)
                .name("Семенова Анна")
                .build();
        final BookingResponseDto testBooking2 = BookingResponseDto.builder()
                .id(2L)
                .start(LocalDateTime.parse("2023-11-30T20:00:00"))
                .end(LocalDateTime.parse("2023-12-02T20:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testBooker)
                .item(testItem2)
                .build();
        final List<BookingResponseDto> testBookings = List.of(testBooking1, testBooking2);

        Mockito.when(bookingService.findAllBookingsByOwnerId(1L, "ALL", 0, 20))
                .thenReturn(testBookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(testBookings)));
    }

    @Test
    @DisplayName("POST /bookings returns HTTP-response with status code 200, content type application/json and " +
            "correct created booking")
    void shouldCreateNewBooking() throws Exception {
        final BookingCreateDto bookingDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2023-12-30T20:00:00"))
                .end(LocalDateTime.parse("2024-01-02T20:00:00"))
                .build();
        final UserBookingDto testBooker = UserBookingDto.builder()
                .id(1L)
                .build();
        final ItemBookingDto testItem = ItemBookingDto.builder()
                .id(1L)
                .name("Семенова Анна")
                .build();
        final BookingResponseDto testBooking = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-11-30T20:00:00"))
                .end(LocalDateTime.parse("2023-12-02T20:00:00"))
                .status(BookingStatus.WAITING)
                .booker(testBooker)
                .item(testItem)
                .build();

        Mockito.when(bookingService.createNewBooking(1L, bookingDto)).thenReturn(testBooking);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testBooking)));
    }

    @Test
    @DisplayName("POST /bookings returns HTTP-response with status code 400, content type application/json and " +
            "error massage, when item is not available")
    void shouldNotCreateNewBookingWithUnavailableItem() throws Exception {
        final BookingCreateDto bookingDto = BookingCreateDto.builder()
                .itemId(2L)
                .start(LocalDateTime.parse("2023-12-12T20:00:00"))
                .end(LocalDateTime.parse("2023-12-22T20:00:00"))
                .build();
        final ExceptionViolation errorResponse = new ExceptionViolation("Item 2 not available");

        Mockito.when(bookingService.createNewBooking(1L, bookingDto)).thenThrow(
                new RequestValidationException("Item 2 not available"));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("POST /bookings returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when item belongs user")
    void shouldNotCreateNewBookingWithItemBelongsUser() throws Exception {
        final BookingCreateDto bookingDto = BookingCreateDto.builder()
                .itemId(2L)
                .start(LocalDateTime.parse("2023-12-12T20:00:00"))
                .end(LocalDateTime.parse("2023-12-22T20:00:00"))
                .build();
        final ExceptionViolation errorResponse = new ExceptionViolation("User 1 is owner of item 2");

        Mockito.when(bookingService.createNewBooking(1L, bookingDto)).thenThrow(
                new AccessNotAllowedException("User 1 is owner of item 2"));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("PATH /bookings/{id} returns HTTP-response with status code 200, content type application/json and " +
            "correct booking with updated status")
    void shouldUpdateBookingStatus() throws Exception {
        final UserBookingDto testBooker = UserBookingDto.builder()
                .id(1L)
                .build();
        final ItemBookingDto testItem = ItemBookingDto.builder()
                .id(1L)
                .name("Семенова Анна")
                .build();
        final BookingResponseDto testBooking = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-11-30T20:00:00"))
                .end(LocalDateTime.parse("2023-12-02T20:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testBooker)
                .item(testItem)
                .build();

        Mockito.when(bookingService.updateBookingStatus(1L, 1L, true)).thenReturn(testBooking);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testBooking)));
    }

    @Test
    @DisplayName("PATH /bookings/{id} returns HTTP-response with status code 200, content type application/json and " +
            "error massage, when booking already approved")
    void shouldNotUpdateBookingStatusWithApprovedStatus() throws Exception {
        final ExceptionViolation errorResponse = new ExceptionViolation("Booking 1 is already approved");

        Mockito.when(bookingService.updateBookingStatus(1L, 1L, true)).thenThrow(
                new RequestValidationException("Booking 1 is already approved"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }
}
