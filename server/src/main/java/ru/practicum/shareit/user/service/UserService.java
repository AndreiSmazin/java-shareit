package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

public interface UserService {
    UserResponseDto findUser(long id);

    List<UserResponseDto> findAllUsers();

    UserResponseDto createNewUser(UserCreateUpdateDto userCreateUpdateDto);

    UserResponseDto updateUser(long id, UserCreateUpdateDto userCreateUpdateDto);

    void deleteUser(long id);

    User checkUser(long id);
}
