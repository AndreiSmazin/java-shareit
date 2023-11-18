package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.List;

@Builder
@Data
public class ExtendedItemForResponseDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentForResponseDto> comments;
}
