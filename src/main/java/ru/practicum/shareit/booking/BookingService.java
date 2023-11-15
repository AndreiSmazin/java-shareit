package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingForRequestDto;
import ru.practicum.shareit.booking.dto.BookingForResponseDto;

import java.util.List;

public interface BookingService {
    BookingForResponseDto findBooking(long userId, long id);

    Booking findBooking(long id);

    List<BookingForResponseDto> findAllBookingsByUserId(long userId, String state);

    List<BookingForResponseDto> findAllBookingsByOwnerId(long userId, String state);

    BookingForResponseDto createNewBooking(long userId, BookingForRequestDto bookingDto);

    BookingForResponseDto updateBookingStatus(long userId, long id, boolean approved);
}
