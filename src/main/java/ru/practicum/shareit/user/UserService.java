package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserRequestCreateDto;
import ru.practicum.shareit.user.dto.UserRequestUpdateDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserDao userDao;

    public User findUser(long id) {
        return userDao.find(id).orElseThrow(() -> new IdNotFoundException("User with this id not exist"));
    }

    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    public User createNewUser(UserRequestCreateDto userDto) {
        log.debug("+ createNewUser: {}", userDto);

        validateEmail(userDto.getEmail());
        User newUser = UserMapper.toUser(userDto);

        return userDao.create(newUser);
    }

    public User updateUser(long id, UserRequestUpdateDto userDto) {
        log.debug("+ updateUser: {}, {}", id, userDto);

        User user = userDao.find(id).orElseThrow(() -> new IdNotFoundException("User with this id not exist"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        String newEmail = userDto.getEmail();
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            validateEmail(newEmail);
            user.setEmail(newEmail);
        }
        userDao.update(user);

        return user;
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
            throw new DuplicateEmailException("User with this email is already exists");
        }
    }
}
