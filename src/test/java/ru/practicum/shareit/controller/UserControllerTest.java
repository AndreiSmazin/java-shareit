package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.Violation;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;

    private final User testUser1 = User.builder()
            .id(1L)
            .name("Иванов Иван")
            .email("IvanovIvan@gmail.com")
            .build();
    private final User testUser2 = User.builder()
            .id(2L)
            .name("Петр Петров")
            .email("nagibator1999@mail.ru")
            .build();

    private final User testUser3 = User.builder()
            .id(3L)
            .name("Гурбангулы Бердымухаммедов")
            .email("superMen@google.com")
            .build();

    @Test
    @DisplayName("GET /users/{id} returns HTTP-response with status code 200, content type application/json and " +
            "correct user")
    void shouldReturnUser() throws Exception {
        Mockito.when(userService.findUser(1L)).thenReturn(testUser1);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(testUser1)));
    }

    @Test
    @DisplayName("GET /users/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when user id is not exist")
    void shouldNotReturnUserWithIdNotExist() throws Exception {
        final long wrongId = 100L;
        final Violation errorResponse = new Violation("id", "User with this id not exist");
        Mockito.when(userService.findUser(wrongId)).thenThrow(new IdNotFoundException("User with this id " +
                "not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + wrongId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("GET /users returns HTTP-response with status code 200, content type application/json and correct " +
                        "list of users")
    void shouldReturnAllUsers() throws Exception {
        final List<User> testUsers = List.of(testUser1, testUser2, testUser3);
        Mockito.when(userService.findAllUsers()).thenReturn(testUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(testUsers)));
    }

    @Test
    @DisplayName("POST /users returns HTTP-response with status code 200, content type application/json and correct " +
            "created user")
    void shouldCreateNewUser() throws Exception {
        final UserDto userDto = UserDto.builder()
                .name("Иванов Иван")
                .email("IvanovIvan@gmail.com")
                .build();
        Mockito.when(userService.createNewUser(userDto)).thenReturn(testUser1);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(testUser1)));
    }

    @Test
    @DisplayName("POST /users returns HTTP-response with status code 400, content type application/json and " +
            "validation error massage, when input user`s fields is incorrect")
    void shouldNotCreateUserWithIncorrectFields() throws Exception {
        final UserDto incorrectUserDto = UserDto.builder()
                .name(" ")
                .email("IvanovIvanIvanovich")
                .build();
        final List<Violation> errorResponse = List.of(new Violation("name", "must not be blank"),
                new Violation("email", "must be a well-formed email address"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incorrectUserDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("POST /users returns HTTP-response with status code 400, content type application/json and " +
            "validation error massage, when input user`s fields is null")
    void shouldNotCreateUserWithNullFields() throws Exception {
        final UserDto incorrectUserDto = UserDto.builder()
                .name(null)
                .email(null)
                .build();
        final List<Violation> errorResponse = List.of(new Violation("name", "must not be blank"),
                new Violation("email", "must not be null"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incorrectUserDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("POST /users returns HTTP-response with status code 409, content type application/json and " +
            "validation error massage, when input user`s email already used")
    void shouldNotCreateUserWithDuplicateEmail() throws Exception {
        final UserDto incorrectUserDto = UserDto.builder()
                .name("Иванов Иван")
                .email("IvanovIvan@gmail.com")
                .build();
        final Violation errorResponse = new Violation("email", "User with this email is already " +
                "exists");
        Mockito.when(userService.createNewUser(incorrectUserDto)).thenThrow(new DuplicateEmailException("User with " +
                "this email is already exists"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incorrectUserDto)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("PATCH /users/{id} returns HTTP-response with status code 200, content type application/json and " +
            "correct changed user")
    void shouldUpdateUser() throws Exception {
        final UserDto userDto = UserDto.builder()
                .name("Иванов Иван")
                .email(null)
                .build();
        Mockito.when(userService.updateUser(testUser1.getId(), userDto)).thenReturn(testUser1);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + testUser1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(testUser1)));
    }

    @Test
    @DisplayName("PATCH /users/{id} returns HTTP-response with status code 400, content type application/json and " +
            "validation error massage, when input user`s email is incorrect")
    void shouldNotUpdateUserWithIncorrectEmail() throws Exception {
        final UserDto incorrectUserDto = UserDto.builder()
                .name(null)
                .email("IvanovIvanIvanovich")
                .build();
        final List<Violation> errorResponse = List.of(new Violation("email", "must be a well-formed " +
                "email address"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + testUser1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incorrectUserDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("PATCH /users/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when user id is not exist")
    void shouldNotUpdateUserWithIdNotExist() throws Exception {
        final long wrongId = 100L;
        final UserDto userDto = UserDto.builder()
                .name("Иванов Иван")
                .email("IvanovIvan@gmail.com")
                .build();
        final Violation errorResponse = new Violation("id", "User with this id not exist");
        Mockito.when(userService.updateUser(wrongId, userDto)).thenThrow(new IdNotFoundException("User with this id " +
                "not exist"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + wrongId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("DELETE /users/{id} returns HTTP-response with status code 200")
    void shouldDeleteUser() throws Exception {
        final long id = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("DELETE /users/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when user id is not exist")
    void shouldNotDeleteUserWithIdNotExist() throws Exception {
        final long wrongId = 100L;
        final Violation errorResponse = new Violation("id", "User with this id not exist");

        Mockito.doThrow(new IdNotFoundException("User with this id not exist")).when(userService).deleteUser(wrongId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + wrongId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(errorResponse)));
    }
}
