package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceInMemoryImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public UserForResponseDto findUser(long id) {
        User user = userDao.find(id).orElseThrow(() ->
                new IdNotFoundException(String.format("User with id %s not exist", id)));

        return userMapper.userToUserForResponseDto(user);
    }

    @Override
    public User checkUser(long id) {
        return userDao.find(id).orElseThrow(() ->
                new IdNotFoundException(String.format("User with id %s not exist", id)));
    }

    @Override
    public List<UserForResponseDto> findAllUsers() {
        return userDao.findAll().stream()
                .map(userMapper::userToUserForResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserForResponseDto createNewUser(UserDto userDto) {
        log.debug("+ createNewUser: {}", userDto);

        validateEmail(userDto.getEmail());
        User user = userMapper.userDtoToUser(userDto);

        return userMapper.userToUserForResponseDto(userDao.create(user));
    }

    @Override
    public UserForResponseDto updateUser(long id, UserDto userDto) {
        log.debug("+ updateUser: {}, {}", id, userDto);

        User targetUser = checkUser(id);
        if (userDto.getName() != null) {
            targetUser.setName(userDto.getName());
        }
        String newEmail = userDto.getEmail();
        if (newEmail != null && !newEmail.equals(targetUser.getEmail())) {
            validateEmail(newEmail);
            targetUser.setEmail(newEmail);
        }
        userDao.update(targetUser);

        return userMapper.userToUserForResponseDto(targetUser);
    }

    @Override
    public void deleteUser(long id) {
        log.debug("+ deleteUser: {}", id);

        userDao.delete(id);
    }

    private void validateEmail(String email) {
        List<String> emails = findAllUsers().stream()
                .map(UserForResponseDto::getEmail)
                .collect(Collectors.toList());

        if (emails.contains(email)) {
            throw new DuplicateEmailException(String.format("User with email %s is already exists", email));
        }
    }
}
