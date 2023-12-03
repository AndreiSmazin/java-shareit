package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface BookingMapper {
    BookingResponseDto bookingToBookingForResponseDto(Booking booking);
}
