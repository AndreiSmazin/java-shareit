package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;

import java.util.List;

public interface UserService {
    UserForResponseDto findUser(long id);

    List<UserForResponseDto> findAllUsers();

    UserForResponseDto createNewUser(UserDto userDto);

    UserForResponseDto updateUser(long id, UserDto userDto);

    void deleteUser(long id);

    User checkUser(long id);
}
