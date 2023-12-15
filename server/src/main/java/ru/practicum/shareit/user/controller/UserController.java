package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponseDto find(@PathVariable long id) {
        return userService.findUser(id);
    }

    @GetMapping
    public List<UserResponseDto> findAll() {
        return userService.findAllUsers();
    }

    @PostMapping
    public UserResponseDto create(@RequestBody UserCreateUpdateDto userCreateUpdateDto) {
        log.debug("Received POST-request /users with body: {}", userCreateUpdateDto);

        return userService.createNewUser(userCreateUpdateDto);
    }

    @PatchMapping("/{id}")
    public UserResponseDto update(@PathVariable long id, @RequestBody UserCreateUpdateDto userCreateUpdateDto) {
        log.debug("Received PATCH-request /users/{} with body: {}", id, userCreateUpdateDto);

        return userService.updateUser(id, userCreateUpdateDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.debug("Received DELETE-request /users/{}", id);

        userService.deleteUser(id);
    }
}
