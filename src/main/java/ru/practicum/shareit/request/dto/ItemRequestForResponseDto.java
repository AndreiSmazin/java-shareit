package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequestForResponseDto {
    private long id;
    private String description;
    private LocalDateTime created;
}
