package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingForResponseDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingForResponseDto bookingToBookingForResponseDto(Booking booking);
}
