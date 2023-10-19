package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserRequestCreateDto;

public class UserMapper {

    public static User toUser(UserRequestCreateDto userRequestCreateDto) {
        return User.builder()
                .id(0)
                .name(userRequestCreateDto.getName())
                .email(userRequestCreateDto.getEmail())
                .build();
    }
}
