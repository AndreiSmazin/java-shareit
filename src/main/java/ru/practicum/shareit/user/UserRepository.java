package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(long id);

    List<User> findAll();

    User save(User user);

    void deleteById(long id);

    Optional<User> findByEmail(String email);
}
