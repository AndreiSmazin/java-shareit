package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequest {
    private long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
