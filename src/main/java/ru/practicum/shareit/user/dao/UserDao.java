package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> find(long id);

    List<User> findAll();

    User create(User user);

    void update(User user);

    void delete(long id);
}
