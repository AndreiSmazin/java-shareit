package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;
import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest
@AutoConfigureTestDatabase
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Test
    @DisplayName("Method findBooking(long userId, long id) should return expected Booking")
    void shouldReturnBookingById() throws Exception {
        final UserBookingDto testBooker = UserBookingDto.builder()
                .id(4L)
                .build();
        final ItemBookingDto testItem = ItemBookingDto.builder()
                .id(3L)
                .name("Байдарка трёхместная Ладога")
                .build();
        final BookingResponseDto expectedBooking = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .status(BookingStatus.APPROVED)
                .item(testItem)
                .booker(testBooker)
                .build();

        final BookingResponseDto bookingForBooker = bookingService.findBooking(4L, 1L);
        final BookingResponseDto bookingForItemOwner = bookingService.findBooking(1L, 1L);

        Assertions.assertEquals(expectedBooking, bookingForBooker, "Booking and expectedBooking is not match");
        Assertions.assertEquals(expectedBooking, bookingForItemOwner, "Booking and expectedBooking is not match");
    }

    @Test
    @DisplayName("Method findBooking(long userId, long id) should throw IdNotFoundException when Booking is not found")
    void shouldThrowExceptionWhenBookingNotFound() throws Exception {
        final String expectedMessage = "Booking with id 100 not exist";

        final IdNotFoundException e = Assertions.assertThrows(
                IdNotFoundException.class,
                () -> bookingService.findBooking(1L, 100L));

        Assertions.assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method findBooking(long userId, long id) should throw AccessNotAllowedException when user is not" +
            " booker or item owner")
    void shouldThrowExceptionWhenUserNotHaveAccess() throws Exception {
        final String expectedMessage = "User 2 does not have access to target booking";

        final AccessNotAllowedException e = Assertions.assertThrows(
                AccessNotAllowedException.class,
                () -> bookingService.findBooking(2L, 1L));

        Assertions.assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method updateBookingStatus(long userId, long id, boolean approved) should return expected Booking" +
            " with updated status")
    void shouldReturnBookingWithUpdatedStatus() throws Exception {
        final UserBookingDto testBooker = UserBookingDto.builder()
                .id(2L)
                .build();
        final ItemBookingDto testItem = ItemBookingDto.builder()
                .id(1L)
                .name("Дрель ударная Bosh")
                .build();
        final BookingResponseDto expectedBooking = BookingResponseDto.builder()
                .id(6L)
                .start(LocalDateTime.parse("2023-12-24T00:00:00"))
                .end(LocalDateTime.parse("2023-12-25T00:00:00"))
                .status(BookingStatus.APPROVED)
                .item(testItem)
                .booker(testBooker)
                .build();

        final BookingResponseDto booking = bookingService.updateBookingStatus(1L, 6L, true);

        Assertions.assertEquals(expectedBooking, booking, "Booking and expectedBooking is not match");
    }

    @Test
    @DisplayName("Method updateBookingStatus(long userId, long id, boolean approved) should throw" +
            " AccessNotAllowedException when user is not owner")
    void shouldThrowExceptionWhenUserNotOwner() throws Exception {
        final String expectedMessage = "User 2 does not have access to target booking";

        final AccessNotAllowedException e = Assertions.assertThrows(
                AccessNotAllowedException.class,
                () -> bookingService.updateBookingStatus(2L, 6L, true));

        Assertions.assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method updateBookingStatus(long userId, long id, boolean approved) should throw" +
            " RequestValidationException when booking already approved")
    void shouldThrowExceptionWhenBookingAlreadyApproved() throws Exception {
        final String expectedMessage = "Booking 1 is already approved";

        final RequestValidationException e = Assertions.assertThrows(
                RequestValidationException.class,
                () -> bookingService.updateBookingStatus(1L, 1L, true));

        Assertions.assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method findAllBookingsByUserId(long userId, String state, int from, int size) should return correct" +
            " list of Bookings with ALL state")
    void shouldReturnAllBookingsByUserId() throws Exception {
        final UserBookingDto testUser = UserBookingDto.builder()
                .id(4L)
                .build();
        final ItemBookingDto testItem = ItemBookingDto.builder()
                .id(3L)
                .name("Байдарка трёхместная Ладога")
                .build();
        final BookingResponseDto expectedBooking1 = BookingResponseDto.builder()
                .id(1)
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testUser)
                .item(testItem)
                .build();
        final BookingResponseDto expectedBooking2 = BookingResponseDto.builder()
                .id(4)
                .start(LocalDateTime.parse("2024-06-15T00:00:00"))
                .end(LocalDateTime.parse("2024-06-20T00:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testUser)
                .item(testItem)
                .build();
        final List<BookingResponseDto> expectedBookings = List.of(expectedBooking2, expectedBooking1);

        final List<BookingResponseDto> bookings = bookingService
                .findAllBookingsByUserId(4L, "ALL", 0, 20);

        Assertions.assertEquals(expectedBookings, bookings, "Bookings and expectedBookings is not match");
    }

    @Test
    @DisplayName("Method findAllBookingsByUserId(long userId, String state, int from, int size) should return correct" +
            " list of Bookings with PAST state")
    void shouldReturnPastBookingsByUserId() throws Exception {
        final UserBookingDto testUser = UserBookingDto.builder()
                .id(4L)
                .build();
        final ItemBookingDto testItem = ItemBookingDto.builder()
                .id(3L)
                .name("Байдарка трёхместная Ладога")
                .build();
        final BookingResponseDto expectedBooking = BookingResponseDto.builder()
                .id(1)
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testUser)
                .item(testItem)
                .build();
        final List<BookingResponseDto> expectedBookings = List.of(expectedBooking);

        final List<BookingResponseDto> bookings = bookingService
                .findAllBookingsByUserId(4L, "PAST", 0, 20);

        Assertions.assertEquals(expectedBookings, bookings, "Bookings and expectedBookings is not match");
    }

    @Test
    @DisplayName("Method findAllBookingsByUserId(long userId, String state, int from, int size) should return correct" +
            " list of Bookings with FUTURE state")
    void shouldReturnFutureBookingsByUserId() throws Exception {
        final UserBookingDto testUser = UserBookingDto.builder()
                .id(4L)
                .build();
        final ItemBookingDto testItem = ItemBookingDto.builder()
                .id(3L)
                .name("Байдарка трёхместная Ладога")
                .build();
        final BookingResponseDto expectedBooking = BookingResponseDto.builder()
                .id(4)
                .start(LocalDateTime.parse("2024-06-15T00:00:00"))
                .end(LocalDateTime.parse("2024-06-20T00:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testUser)
                .item(testItem)
                .build();
        final List<BookingResponseDto> expectedBookings = List.of(expectedBooking);

        final List<BookingResponseDto> bookings = bookingService
                .findAllBookingsByUserId(4L, "FUTURE", 0, 20);

        Assertions.assertEquals(expectedBookings, bookings, "Bookings and expectedBookings is not match");
    }

    @Test
    @DisplayName("Method findAllBookingsByUserId(long userId, String state, int from, int size) should return empty" +
            " list of Bookings with CURRENT, REJECTED and WAITING states")
    void shouldReturnEmptyListWhenStateCurrentRejectedWaiting() throws Exception {
        final List<BookingResponseDto> currentBookings = bookingService
                .findAllBookingsByUserId(4L, "CURRENT", 0, 20);

        Assertions.assertTrue(currentBookings.isEmpty(), "Bookings list is not empty");

        final List<BookingResponseDto> rejectedBookings = bookingService
                .findAllBookingsByUserId(4L, "REJECTED", 0, 20);

        Assertions.assertTrue(rejectedBookings.isEmpty(), "Bookings list is not empty");

        final List<BookingResponseDto> waitingBookings = bookingService
                .findAllBookingsByUserId(4L, "WAITING", 0, 20);

        Assertions.assertTrue(waitingBookings.isEmpty(), "Bookings list is not empty");
    }

    @Test
    @DisplayName("Method findAllBookingsByOwnerId(long userId, String state, int from, int size) should return correct" +
            " list of Bookings with ALL state")
    void shouldReturnAllBookingsByOwnerId() throws Exception {
        final UserBookingDto testUser = UserBookingDto.builder()
                .id(2L)
                .build();
        final ItemBookingDto testItem = ItemBookingDto.builder()
                .id(2L)
                .name("Дрель аккумуляторная")
                .build();
        final BookingResponseDto expectedBooking = BookingResponseDto.builder()
                .id(5)
                .start(LocalDateTime.parse("2023-11-01T00:00:00"))
                .end(LocalDateTime.parse("2023-11-10T00:00:00"))
                .status(BookingStatus.APPROVED)
                .booker(testUser)
                .item(testItem)
                .build();
        final List<BookingResponseDto> expectedBookings = List.of(expectedBooking);

        final List<BookingResponseDto> bookings = bookingService
                .findAllBookingsByOwnerId(3L, "ALL", 0, 20);

        Assertions.assertEquals(expectedBookings, bookings, "Bookings and expectedBookings is not match");
    }
}
