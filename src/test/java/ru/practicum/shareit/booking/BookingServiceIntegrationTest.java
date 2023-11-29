package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingForResponseDto;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.user.dto.UserForBookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Test
    @DisplayName("Method findBooking(long userId, long id) should return expected Booking")
    void shouldReturnBookingById() throws Exception {
        final UserForBookingDto testBooker = UserForBookingDto.builder()
                .id(4L)
                .build();
        final ItemForBookingDto testItem = ItemForBookingDto.builder()
                .id(3L)
                .name("Байдарка трёхместная Ладога")
                .build();
        final BookingForResponseDto expectedBooking = BookingForResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .status(BookingStatus.APPROVED)
                .item(testItem)
                .booker(testBooker)
                .build();

        final BookingForResponseDto bookingForBooker = bookingService.findBooking(4L, 1L);
        final BookingForResponseDto bookingForItemOwner = bookingService.findBooking(1L, 1L);

        assertEquals(expectedBooking, bookingForBooker, "Booking and expectedBooking is not match");
        assertEquals(expectedBooking, bookingForItemOwner, "Booking and expectedBooking is not match");
    }

    @Test
    @DisplayName("Method findBooking(long userId, long id) should throw IdNotFoundException when Booking is not found")
    void shouldThrowExceptionWhenBookingNotFound() throws Exception {
        final String expectedMessage = "Booking with id 100 not exist";

        final IdNotFoundException e = assertThrows(
                IdNotFoundException.class,
                () -> bookingService.findBooking(1L, 100L));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method findBooking(long userId, long id) should throw AccessNotAllowedException when user is not" +
            " booker or item owner")
    void shouldThrowExceptionWhenUserNotHaveAccess() throws Exception {
        final String expectedMessage = "User 2 does not have access to target booking";

        final AccessNotAllowedException e = assertThrows(
                AccessNotAllowedException.class,
                () -> bookingService.findBooking(2L, 1L));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method updateBookingStatus(long userId, long id, boolean approved) should return expected Booking" +
            " with updated status")
    void shouldReturnBookingWithUpdatedStatus() throws Exception {
        final UserForBookingDto testBooker = UserForBookingDto.builder()
                .id(2L)
                .build();
        final ItemForBookingDto testItem = ItemForBookingDto.builder()
                .id(1L)
                .name("Дрель ударная Bosh")
                .build();
        final BookingForResponseDto expectedBooking = BookingForResponseDto.builder()
                .id(6L)
                .start(LocalDateTime.parse("2023-12-24T00:00:00"))
                .end(LocalDateTime.parse("2023-12-25T00:00:00"))
                .status(BookingStatus.APPROVED)
                .item(testItem)
                .booker(testBooker)
                .build();

        final BookingForResponseDto booking = bookingService.updateBookingStatus(1L, 6L, true);

        assertEquals(expectedBooking, booking, "Booking and expectedBooking is not match");
    }

    @Test
    @DisplayName("Method updateBookingStatus(long userId, long id, boolean approved) should throw" +
            " AccessNotAllowedException when user is not owner")
    void shouldThrowExceptionWhenUserNotOwner() throws Exception {
        final String expectedMessage = "User 2 does not have access to target booking";

        final AccessNotAllowedException e = assertThrows(
                AccessNotAllowedException.class,
                () -> bookingService.updateBookingStatus(2L, 6L, true));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method updateBookingStatus(long userId, long id, boolean approved) should throw" +
            " RequestValidationException when booking already approved")
    void shouldThrowExceptionWhenBookingAlreadyApproved() throws Exception {
        final String expectedMessage = "Booking 1 is already approved";

        final RequestValidationException e = assertThrows(
                RequestValidationException.class,
                () -> bookingService.updateBookingStatus(1L, 1L, true));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }
}
