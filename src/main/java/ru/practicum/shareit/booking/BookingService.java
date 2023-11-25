package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingForRequestDto;
import ru.practicum.shareit.booking.dto.BookingForResponseDto;

import java.util.List;

public interface BookingService {
    BookingForResponseDto findBooking(long userId, long id);

    Booking checkBooking(long id);

    List<BookingForResponseDto> findAllBookingsByUserId(long userId, String state, int from, int size);

    List<BookingForResponseDto> findAllBookingsByOwnerId(long userId, String state, int from, int size);

    BookingForResponseDto createNewBooking(long userId, BookingForRequestDto bookingDto);

    BookingForResponseDto updateBookingStatus(long userId, long id, boolean approved);
}
