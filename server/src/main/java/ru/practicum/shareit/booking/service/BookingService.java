package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto findBooking(long userId, long id);

    Booking checkBooking(long id);

    List<BookingResponseDto> findAllBookingsByUserId(long userId, String state, int from, int size);

    List<BookingResponseDto> findAllBookingsByOwnerId(long userId, String state, int from, int size);

    BookingResponseDto createNewBooking(long userId, BookingCreateDto bookingDto);

    BookingResponseDto updateBookingStatus(long userId, long id, boolean approved);
}
