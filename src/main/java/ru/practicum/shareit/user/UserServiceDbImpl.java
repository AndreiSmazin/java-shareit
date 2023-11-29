package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;

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
    public UserForResponseDto findUser(long id) {
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
    public List<UserForResponseDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserForResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserForResponseDto createNewUser(UserDto userDto) {
        log.debug("+ createNewUser: {}", userDto);

        User user = userMapper.userDtoToUser(userDto);

        return userMapper.userToUserForResponseDto(userRepository.save(user));
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
