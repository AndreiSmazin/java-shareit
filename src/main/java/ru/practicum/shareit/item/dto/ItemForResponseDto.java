package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemForResponseDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long requestId;
}
