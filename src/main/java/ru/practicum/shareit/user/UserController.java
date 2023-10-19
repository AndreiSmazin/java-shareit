package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestCreateDto;
import ru.practicum.shareit.user.dto.UserRequestUpdateDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public User find(@PathVariable long id) {
        return userService.findUser(id);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAllUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody UserRequestCreateDto userDto) {
        log.info("Received POST-request /users with body: {}", userDto);

        return userService.createNewUser(userDto);
    }

    @PatchMapping("/{id}")
    public User update(@Valid @PathVariable long id, @RequestBody UserRequestUpdateDto userDto) {
        log.info("Received PATCH-request /users/{} with body: {}", id, userDto);

        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Received DELETE-request /users/{}", id);

        userService.deleteUser(id);
    }
}
