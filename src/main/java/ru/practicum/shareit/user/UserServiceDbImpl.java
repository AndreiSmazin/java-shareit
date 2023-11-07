package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Qualifier("userServiceDbImpl")
@Slf4j
public class UserServiceDbImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User findUser(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("User with id %s not exist", id)));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createNewUser(UserDto userDto) {
        log.debug("+ createNewUser: {}", userDto);

        validateEmail(userDto.getEmail());
        User user = userMapper.userDtoToUser(userDto);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(long id, UserDto userDto) {
        log.debug("+ updateUser: {}, {}", id, userDto);

        User targetUser = findUser(id);
        if (userDto.getName() != null) {
            targetUser.setName(userDto.getName());
        }
        String newEmail = userDto.getEmail();
        if (newEmail != null && !newEmail.equals(targetUser.getEmail())) {
            validateEmail(newEmail);
            targetUser.setEmail(newEmail);
        }

        return userRepository.save(targetUser);
    }

    @Override
    public void deleteUser(long id) {
        log.debug("+ deleteUser: {}", id);

        userRepository.deleteById(id);
    }

    private void validateEmail(String email) {
        List<String> emails = findAllUsers().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        if (emails.contains(email)) {
            throw new DuplicateEmailException(String.format("User with email %s is already exists", email));
        }
    }
}
