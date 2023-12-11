package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class UserCreateUpdateDto {
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
    @NotNull(groups = Marker.OnCreate.class)
    @Email(groups = Marker.OnCreate.class)
    @Email(groups = Marker.OnUpdate.class)
    private String email;
}
