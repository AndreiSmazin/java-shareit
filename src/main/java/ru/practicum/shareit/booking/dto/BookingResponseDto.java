package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingResponseDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserBookingDto booker;
    private ItemBookingDto item;
}
