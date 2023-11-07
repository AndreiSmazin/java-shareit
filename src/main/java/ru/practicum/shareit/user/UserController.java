package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier("userServiceDbImpl") UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User find(@PathVariable long id) {
        return userService.findUser(id);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAllUsers();
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public User create(@Valid @RequestBody UserDto userDto) {
        log.debug("Received POST-request /users with body: {}", userDto);

        return userService.createNewUser(userDto);
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public User update(@PathVariable long id, @Valid @RequestBody UserDto userDto) {
        log.debug("Received PATCH-request /users/{} with body: {}", id, userDto);

        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.debug("Received DELETE-request /users/{}", id);

        userService.deleteUser(id);
    }
}
