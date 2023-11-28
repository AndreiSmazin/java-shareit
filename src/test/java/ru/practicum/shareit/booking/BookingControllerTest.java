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
import ru.practicum.shareit.booking.dto.BookingForRequestDto;
import ru.practicum.shareit.booking.dto.BookingForResponseDto;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.ExceptionViolation;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.exception.ValidationViolation;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.user.dto.UserForBookingDto;

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
        final UserForBookingDto testBooker = UserForBookingDto.builder()
                .id(1L)
                .build();
        final ItemForBookingDto testItem = ItemForBookingDto.builder()
                .id(1L)
                .name("Петров Алексей")
                .build();
        final BookingForResponseDto testBooking = BookingForResponseDto.builder()
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
        final UserForBookingDto testBooker = UserForBookingDto.builder()
                .id(1L)
                .build();
        final ItemForBookingDto testItem1 = ItemForBookingDto.builder()
                .id(1L)
                .name("Семенова Анна")
                .build();
        final BookingForResponseDto testBooking1 = BookingForResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-11-26T20:00:00"))
                .end(LocalDateTime.parse("2023-11-30T20:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testBooker)
                .item(testItem1)
                .build();
        final ItemForBookingDto testItem2 = ItemForBookingDto.builder()
                .id(2L)
                .name("Петров Алексей")
                .build();
        final BookingForResponseDto testBooking2 = BookingForResponseDto.builder()
                .id(2L)
                .start(LocalDateTime.parse("2023-11-30T20:00:00"))
                .end(LocalDateTime.parse("2023-12-02T20:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testBooker)
                .item(testItem2)
                .build();
        final List<BookingForResponseDto> testBookings = List.of(testBooking1, testBooking2);

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
    @DisplayName("GET /bookings returns HTTP-response with status code 400, content type application/json and " +
            "error massage, when request params is wrong")
    void shouldNotReturnAllBookingsOfUserWithWrongRequestParams() throws Exception {
        final List<ValidationViolation> errorResponse = List.of(
                new ValidationViolation("from", "must be greater than or equal to 0"),
                new ValidationViolation("size", "must be less than or equal to 100"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings?state=CURRENT&from=-1&size=250")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("GET /bookings returns HTTP-response with status code 200, content type application/json and " +
            "correct list of bookings")
    void shouldReturnAllBookingsOfOwner() throws Exception {
        final UserForBookingDto testBooker = UserForBookingDto.builder()
                .id(1L)
                .build();
        final ItemForBookingDto testItem1 = ItemForBookingDto.builder()
                .id(1L)
                .name("Семенова Анна")
                .build();
        final BookingForResponseDto testBooking1 = BookingForResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-11-26T20:00:00"))
                .end(LocalDateTime.parse("2023-11-30T20:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testBooker)
                .item(testItem1)
                .build();
        final ItemForBookingDto testItem2 = ItemForBookingDto.builder()
                .id(2L)
                .name("Семенова Анна")
                .build();
        final BookingForResponseDto testBooking2 = BookingForResponseDto.builder()
                .id(2L)
                .start(LocalDateTime.parse("2023-11-30T20:00:00"))
                .end(LocalDateTime.parse("2023-12-02T20:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testBooker)
                .item(testItem2)
                .build();
        final List<BookingForResponseDto> testBookings = List.of(testBooking1, testBooking2);

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
        final BookingForRequestDto bookingDto = BookingForRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2023-11-30T20:00:00"))
                .end(LocalDateTime.parse("2023-12-02T20:00:00"))
                .build();
        final UserForBookingDto testBooker = UserForBookingDto.builder()
                .id(1L)
                .build();
        final ItemForBookingDto testItem = ItemForBookingDto.builder()
                .id(1L)
                .name("Семенова Анна")
                .build();
        final BookingForResponseDto testBooking = BookingForResponseDto.builder()
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
            "error massage, when start or end date is wrong")
    void shouldNotCreateNewBookingWithWrongDates() throws Exception {
        final BookingForRequestDto bookingDto1 = BookingForRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2022-11-30T20:00:00"))
                .end(LocalDateTime.parse("2022-12-02T20:00:00"))
                .build();
        final List<ValidationViolation> errorResponse1 = List.of(
                new ValidationViolation("start", "must be a date in the present or in the future"),
                new ValidationViolation("end", "must be a date in the present or in the future"));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto1)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse1)));

        final BookingForRequestDto bookingDto2 = BookingForRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2023-12-12T20:00:00"))
                .end(LocalDateTime.parse("2023-12-02T20:00:00"))
                .build();
        final ExceptionViolation errorResponse2 = new ExceptionViolation("End booking date 2023-12-02T20:00:00" +
                " can`t be earlier than start date 2023-12-12T20:00:00");

        Mockito.when(bookingService.createNewBooking(1L, bookingDto2)).thenThrow(
                new RequestValidationException("End booking date 2023-12-02T20:00:00 can`t be earlier than start date" +
                        " 2023-12-12T20:00:00"));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse2)));
    }

    @Test
    @DisplayName("POST /bookings returns HTTP-response with status code 400, content type application/json and " +
            "error massage, when item is not available")
    void shouldNotCreateNewBookingWithUnavailableItem() throws Exception {
        final BookingForRequestDto bookingDto = BookingForRequestDto.builder()
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
        final BookingForRequestDto bookingDto = BookingForRequestDto.builder()
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
        final UserForBookingDto testBooker = UserForBookingDto.builder()
                .id(1L)
                .build();
        final ItemForBookingDto testItem = ItemForBookingDto.builder()
                .id(1L)
                .name("Семенова Анна")
                .build();
        final BookingForResponseDto testBooking = BookingForResponseDto.builder()
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
