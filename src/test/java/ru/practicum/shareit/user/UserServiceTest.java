package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceDbImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    private UserMapper userMapper = new UserMapperImpl();
    private UserService userService;

    @BeforeEach
    void initiateUserService() {
        userService = new UserServiceDbImpl(userRepository, userMapper);
    }

    @Test
    @DisplayName("Method checkUser(long id) should return correct User")
    void shouldReturnUser() throws Exception {
        final User expectedUser = User.builder()
                .id(3L)
                .name("Сергей Иванов")
                .email("SupremeSerg91@yandex.com")
                .build();

        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.of(expectedUser));

        final User user = userService.checkUser(3L);

        assertEquals(expectedUser, user, "User and expectedUser is not match");
    }

    @Test
    @DisplayName("Method checkUser(long id) should throw IdNotFoundException when User is not found")
    void shouldThrowExceptionWhenUserNotFound() throws Exception {
        final String expectedMessage = "User with id 10 not exist";

        Mockito.when(userRepository.findById(10L)).thenReturn(Optional.empty());

        final Exception e = assertThrows(IdNotFoundException.class, () ->
                userService.checkUser(10L));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method findAllUsers() should return correct list of users")
    void shouldReturnListOfUsers() throws Exception {
        final User testUser1 = User.builder()
                .id(1L)
                .name("Сергей Иванов")
                .email("SupremeSerg91@yandex.com")
                .build();
        final User testUser2 = User.builder()
                .id(2L)
                .name("Ирина Васильева")
                .email("BobrIrina@google.com")
                .build();
        final List<UserResponseDto> expectedUsers = Stream.of(testUser1, testUser2)
                .map(userMapper::userToUserForResponseDto)
                .collect(Collectors.toList());

        Mockito.when(userRepository.findAll()).thenReturn(List.of(testUser1, testUser2));

        final List<UserResponseDto> users = userService.findAllUsers();

        assertEquals(expectedUsers, users, "UsersIds massage and expectedUsersIds is not match");
    }

    @Test
    @DisplayName("Method createNewUser(UserDto userDto) should return correct created User")
    void shouldReturnCreatedUser() throws Exception {
        final UserResponseDto expectedUser = UserResponseDto.builder()
                .id(1L)
                .name("Сергей Иванов")
                .email("SupremeSerg91@yandex.com")
                .build();
        final UserCreateUpdateDto userCreateUpdateDto = UserCreateUpdateDto.builder()
                .name("Сергей Иванов")
                .email("SupremeSerg91@yandex.com")
                .build();

        final User newUser = User.builder()
                .id(1L)
                .name("Сергей Иванов")
                .email("SupremeSerg91@yandex.com")
                .build();
        Mockito.when(userRepository.save(userMapper.userDtoToUser(userCreateUpdateDto))).thenReturn(newUser);

        final UserResponseDto createdUser = userService.createNewUser(userCreateUpdateDto);

        assertEquals(expectedUser, createdUser, "User and expectedUser is not match");
    }
}
