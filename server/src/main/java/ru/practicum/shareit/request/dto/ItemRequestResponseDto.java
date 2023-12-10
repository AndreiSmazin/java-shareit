package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequestResponseDto {
    private long id;
    private String description;
    private LocalDateTime created;
}
