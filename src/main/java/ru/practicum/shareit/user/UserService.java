package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

    public User findUser(long id) {
        return userDao.find(id).orElseThrow(() ->
                new IdNotFoundException(String.format("User with id %s not exist", id)));
    }

    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    public User createNewUser(UserDto userDto) {
        log.debug("+ createNewUser: {}", userDto);

        validateEmail(userDto.getEmail());
        User user = userMapper.userDtoToUser(userDto);

        return userDao.create(user);
    }

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
        userDao.update(targetUser);

        return targetUser;
    }

    public void deleteUser(long id) {
        log.debug("+ deleteUser: {}", id);

        userDao.delete(id);
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
