package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("Method findAllByBookerIdOrderByStartDesc(long id) should return correct list of bookings")
    void shouldReturnAllBookingsByBookerId() throws Exception {
        final List<Long> expectedBookingsIds = List.of(4L, 1L);

        final List<Long> bookingsIds = bookingRepository.findAllByBookerIdOrderByStartDesc(4L).stream()
                .map(Booking::getId)
                .collect(Collectors.toList());

        assertEquals(expectedBookingsIds, bookingsIds, "BookingsIds and expectedBookingsIds is not match");
    }

    @Test
    @DisplayName("Method findAllByItemOwnerIdOrderByStartDesc(long id) should return correct list of bookings")
    void shouldReturnAllBookingsByItemOwnerId() throws Exception {
        final List<Long> expectedBookingsIds = List.of(4L, 3L, 6L, 1L, 2L);

        final List<Long> bookingsIds = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(1L).stream()
                .map(Booking::getId)
                .collect(Collectors.toList());

        assertEquals(expectedBookingsIds, bookingsIds, "BookingsIds and expectedBookingsIds is not match");
    }

    @Test
    @DisplayName("Method findPastBookingsOfItem(long id) should return correct list of bookings")
    void shouldReturnPastBookingsOfItem() throws Exception {
        final List<Long> expectedBookingsIds = List.of(1L, 2L);

        final List<Long> bookingsIds = bookingRepository.findPastBookingsOfItem(3L).stream()
                .map(BookingItemDto::getId)
                .collect(Collectors.toList());

        assertEquals(expectedBookingsIds, bookingsIds, "BookingsIds and expectedBookingsIds is not match");
    }

    @Test
    @DisplayName("Method findFutureBookingsOfItem(long id) should return correct list of bookings")
    void shouldReturnFutureBookingsOfItem() throws Exception {
        final List<Long> expectedBookingsIds = List.of(3L, 4L);

        final List<Long> bookingsIds = bookingRepository.findFutureBookingsOfItem(3L).stream()
                .map(BookingItemDto::getId)
                .collect(Collectors.toList());

        assertEquals(expectedBookingsIds, bookingsIds, "BookingsIds and expectedBookingsIds is not match");
    }

    @Test
    @DisplayName("Method findCountBookingsOfUser(long userId, long itemId) should return correct count of bookings")
    void shouldReturnCountOfBookingsForUser() throws Exception {
        final int expectedCount = 1;

        final int count = bookingRepository.findCountBookingsOfUser(4L, 3L);

        assertEquals(expectedCount, count, "Count and expectedCount is not match");
    }
}
