package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserForRequestCreateDto;
import ru.practicum.shareit.user.dto.UserForRequestUpdateDto;

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
    public User create(@Valid @RequestBody UserForRequestCreateDto userDto) {
        log.info("Received POST-request /users with body: {}", userDto);

        return userService.createNewUser(userDto);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable long id, @Valid @RequestBody UserForRequestUpdateDto userDto) {
        log.info("Received PATCH-request /users/{} with body: {}", id, userDto);

        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Received DELETE-request /users/{}", id);

        userService.deleteUser(id);
    }
}
