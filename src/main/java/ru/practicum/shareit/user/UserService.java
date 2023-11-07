package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User findUser(long id);

    List<User> findAllUsers();

    User createNewUser(UserDto userDto);

    User updateUser(long id, UserDto userDto);

    public void deleteUser(long id);
}
