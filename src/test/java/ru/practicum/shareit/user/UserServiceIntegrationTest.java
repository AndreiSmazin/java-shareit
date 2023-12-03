package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceDbImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceIntegrationTest {
    private final UserService userService;

    @Autowired
    public UserServiceIntegrationTest(UserServiceDbImpl userService) {
        this.userService = userService;
    }

    @Test
    @DisplayName("Method findUser(long id) should return expected user")
    void shouldReturnUserById() throws Exception {
        final UserResponseDto expectedUser = UserResponseDto.builder()
                .id(3L)
                .name("Сергей Иванов")
                .email("SupremeSerg91@yandex.com")
                .build();

        final UserResponseDto user = userService.findUser(3L);

        assertEquals(expectedUser, user, "User and expectedUser is not match");
    }

    @Test
    @DisplayName("Method findUser(long id) should throw IdNotFoundException when User is not found")
    void shouldThrowExceptionWhenUserNotFound() throws Exception {
        final String expectedMessage = "User with id 10 not exist";

        final IdNotFoundException e = assertThrows(
                IdNotFoundException.class,
                () -> userService.findUser(10L));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method updateUser(long id, UserDto userDto) should return updated user")
    void shouldUpdateUser() throws Exception {
        final UserResponseDto expectedUser = UserResponseDto.builder()
                .id(2L)
                .name("Ирина Васильева")
                .email("BobrIrina@google.com")
                .build();
        final UserCreateUpdateDto testUserCreateUpdateDto = UserCreateUpdateDto.builder()
                .name("Ирина Васильева")
                .email("BobrIrina@google.com")
                .build();

        final UserResponseDto user = userService.updateUser(2L, testUserCreateUpdateDto);

        assertEquals(expectedUser, user, "User and expectedUser is not match");
    }

    @Test
    @DisplayName("Method updateUser(long id, UserDto userDto) should throw IdNotFoundException when User is not found")
    void shouldThrowExceptionWhenUserForUpdateNotFound() throws Exception {
        final String expectedMessage = "User with id 10 not exist";
        final UserCreateUpdateDto testUserCreateUpdateDto = UserCreateUpdateDto.builder()
                .name("Ирина Васильева")
                .email("BobrIrina@google.com")
                .build();

        final IdNotFoundException e = assertThrows(
                IdNotFoundException.class,
                () -> userService.updateUser(10L, testUserCreateUpdateDto));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }
}
