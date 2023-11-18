package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForBookingDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {
    User userDtoToUser(UserDto userDto);

    UserForBookingDto userToUserForBookingDto(User user);

    UserForResponseDto userToUserForResponseDto(User user);
}
