package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Builder
@Data
public class UserRequestUpdateDto {
    private String name;
    @Email
    private String email;
}
