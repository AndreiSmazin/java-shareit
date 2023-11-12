package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingForRequestDto;

import java.util.List;

public interface BookingService {
    Booking findBooking(long userId, long id);

    Booking findBooking(long id);

    List<Booking> findAllBookingsByUserId(long userId, String state);

    List<Booking> findAllBookingsByOwnerId(long userId, String state);

    Booking createNewBooking(long userId, BookingForRequestDto bookingDto);

    Booking updateBookingStatus(long userId, long id, boolean approved);
}
