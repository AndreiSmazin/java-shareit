package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserForRequestCreateDto;

public class UserMapper {

    public static User toUser(UserForRequestCreateDto userDto) {
        return User.builder()
                .id(0)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
