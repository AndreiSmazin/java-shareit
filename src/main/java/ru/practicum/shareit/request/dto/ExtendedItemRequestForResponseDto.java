package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ExtendedItemRequestForResponseDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForResponseDto> items;
}
