package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.List;

@Builder
@Data
public class ItemExtendedResponseDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<CommentResponseDto> comments;
}
