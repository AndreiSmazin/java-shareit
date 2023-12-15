package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment as c" +
            " join fetch c.author as a" +
            " join c.item as i" +
            " where i.id = ?1")
    List<Comment> findAllByItemId(Long itemId);
}
