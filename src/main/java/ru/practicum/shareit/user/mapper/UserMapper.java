package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {
    User userDtoToUser(UserCreateUpdateDto userCreateUpdateDto);

    UserBookingDto userToUserForBookingDto(User user);

    UserResponseDto userToUserForResponseDto(User user);
}
