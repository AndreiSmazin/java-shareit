package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceDbImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto findUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("User with id %s not exist", id)));

        return userMapper.userToUserForResponseDto(user);
    }

    @Override
    public User checkUser(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("User with id %s not exist", id)));
    }

    @Override
    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserForResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto createNewUser(UserCreateUpdateDto userCreateUpdateDto) {
        log.debug("+ createNewUser: {}", userCreateUpdateDto);

        User user = userMapper.userDtoToUser(userCreateUpdateDto);

        return userMapper.userToUserForResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateUser(long id, UserCreateUpdateDto userCreateUpdateDto) {
        log.debug("+ updateUser: {}, {}", id, userCreateUpdateDto);

        User targetUser = checkUser(id);
        if (userCreateUpdateDto.getName() != null) {
            targetUser.setName(userCreateUpdateDto.getName());
        }
        String newEmail = userCreateUpdateDto.getEmail();
        if (newEmail != null && !newEmail.equals(targetUser.getEmail())) {
            targetUser.setEmail(newEmail);
        }

        return userMapper.userToUserForResponseDto(userRepository.save(targetUser));
    }

    @Override
    public void deleteUser(long id) {
        log.debug("+ deleteUser: {}", id);

        userRepository.deleteById(id);
    }
}
