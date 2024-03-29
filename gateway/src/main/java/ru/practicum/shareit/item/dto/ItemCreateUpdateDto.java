package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class ItemCreateUpdateDto {
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
    private Long requestId;
}
