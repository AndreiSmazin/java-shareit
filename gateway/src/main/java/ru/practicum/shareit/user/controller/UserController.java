package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.Marker;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> find(@PathVariable long id) {
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.getAllUsers();
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreateUpdateDto userCreateUpdateDto) {
        log.debug("Received POST-request /users with body: {}", userCreateUpdateDto);

        return userClient.createNewUser(userCreateUpdateDto);
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<Object> update(@PathVariable long id,
                                         @Valid @RequestBody UserCreateUpdateDto userCreateUpdateDto) {
        log.debug("Received PATCH-request /users/{} with body: {}", id, userCreateUpdateDto);

        return userClient.updateUser(id, userCreateUpdateDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.debug("Received DELETE-request /users/{}", id);

        userClient.deleteUser(id);
    }
}
