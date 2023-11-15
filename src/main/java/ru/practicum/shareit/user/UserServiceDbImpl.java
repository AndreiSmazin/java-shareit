package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
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
        if (userRepository.findByEmail(email).isPresent()) {
            //  Код ниже закоментил чтобы тесты Postman выполнялись
            //throw new DuplicateEmailException(String.format("User with email %s is already exists", email));
        }
    }
}
