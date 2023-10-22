package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class UserDaoInMemoryImpl implements UserDao{
    private Map<Long, User> users = new HashMap<>();
    private long currentId = 1;

    @Override
    public Optional<User> find(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        log.debug("+ create User: {}", user);

        user.setId(currentId++);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public void update(User user) {
        log.debug("+ update User: {}", user);

        users.put(user.getId(), user);
    }

    @Override
    public void delete(long id) {
        log.debug("+ delete User: {}", id);

        users.remove(id);
    }
}
