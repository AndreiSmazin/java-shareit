package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

@Builder
@Data
public class ItemForResponseWithBookingsDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
}
