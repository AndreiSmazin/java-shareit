package ru.practicum.shareit.user;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.ExceptionViolation;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ValidationViolation;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;

import java.util.List;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /users/{id} returns HTTP-response with status code 200, content type application/json and" +
            " correct user")
    void shouldReturnUser() throws Exception {
        final UserForResponseDto testUser = UserForResponseDto.builder()
                .id(1L)
                .name("Иванов Иван")
                .email("IvanovIvan@gmail.com")
                .build();

        Mockito.when(userService.findUser(1L)).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testUser)));
    }

    @Test
    @DisplayName("GET /users/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when user id is not exist")
    void shouldNotReturnUserWithIdNotExist() throws Exception {
        final ExceptionViolation errorResponse = new ExceptionViolation("User with id 100 not exist");

        Mockito.when(userService.findUser(100L)).thenThrow(new IdNotFoundException("User with id 100 not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/100"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("GET /users returns HTTP-response with status code 200, content type application/json and correct " +
            "list of users")
    void shouldReturnAllUsers() throws Exception {
        final UserForResponseDto testUser1 = UserForResponseDto.builder()
                .id(1L)
                .name("Иванов Иван")
                .email("IvanovIvan@gmail.com")
                .build();
        final UserForResponseDto testUser2 = UserForResponseDto.builder()
                .id(2L)
                .name("Петр Петров")
                .email("nagibator1999@mail.ru")
                .build();
        final List<UserForResponseDto> testUsers = List.of(testUser1, testUser2);

        Mockito.when(userService.findAllUsers()).thenReturn(testUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testUsers)));
    }

    @Test
    @DisplayName("POST /users returns HTTP-response with status code 200, content type application/json and correct " +
            "created user")
    void shouldCreateNewUser() throws Exception {
        final UserDto userDto = UserDto.builder()
                .name("Иванов Иван")
                .email("IvanovIvan@gmail.com")
                .build();
        final UserForResponseDto testUser = UserForResponseDto.builder()
                .id(1L)
                .name("Иванов Иван")
                .email("IvanovIvan@gmail.com")
                .build();

        Mockito.when(userService.createNewUser(userDto)).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testUser)));
    }

    @Test
    @DisplayName("POST /users returns HTTP-response with status code 400, content type application/json and " +
            "validation error massage, when input user`s fields is incorrect")
    void shouldNotCreateUserWithIncorrectFields() throws Exception {
        final UserDto incorrectUserDto = UserDto.builder()
                .name(" ")
                .email("IvanovIvanIvanovich")
                .build();
        final List<ValidationViolation> errorResponse = List.of(
                new ValidationViolation("name", "must not be blank"),
                new ValidationViolation("email", "must be a well-formed email address"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incorrectUserDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("POST /users returns HTTP-response with status code 400, content type application/json and " +
            "validation error massage, when input user`s fields is null")
    void shouldNotCreateUserWithNullFields() throws Exception {
        final UserDto incorrectUserDto = UserDto.builder()
                .name(null)
                .email(null)
                .build();
        final List<ValidationViolation> errorResponse = List.of(
                new ValidationViolation("name", "must not be blank"),
                new ValidationViolation("email", "must not be null"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incorrectUserDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("PATCH /users/{id} returns HTTP-response with status code 200, content type application/json and " +
            "correct changed user")
    void shouldUpdateUser() throws Exception {
        final UserDto userDto = UserDto.builder()
                .name("Иванов Иван")
                .email(null)
                .build();
        final UserForResponseDto testUser = UserForResponseDto.builder()
                .id(1L)
                .name("Иванов Иван")
                .email("IvanovIvan@gmail.com")
                .build();

        Mockito.when(userService.updateUser(1L, userDto)).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(testUser)));
    }

    @Test
    @DisplayName("PATCH /users/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when user id is not exist")
    void shouldNotUpdateUserWithIdNotExist() throws Exception {
        final UserDto userDto = UserDto.builder()
                .name("Иванов Иван")
                .email("IvanovIvan@gmail.com")
                .build();
        final ExceptionViolation errorResponse = new ExceptionViolation("User with id 100 not exist");

        Mockito.when(userService.updateUser(100L, userDto))
                .thenThrow(new IdNotFoundException("User with id 100 not exist"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }

    @Test
    @DisplayName("DELETE /users/{id} returns HTTP-response with status code 200")
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("DELETE /users/{id} returns HTTP-response with status code 404, content type application/json and " +
            "error massage, when user id is not exist")
    void shouldNotDeleteUserWithIdNotExist() throws Exception {
        final ExceptionViolation errorResponse = new ExceptionViolation("User with id 100 not exist");

        Mockito.doThrow(new IdNotFoundException("User with id 100 not exist")).when(userService).deleteUser(100L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/100"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(errorResponse)));
    }
}
