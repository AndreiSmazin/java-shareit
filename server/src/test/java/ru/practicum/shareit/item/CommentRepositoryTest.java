package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("Method findAllByItemId(Long itemId) should return correct list of comments")
    void shouldReturnAllCommentsByItemId() throws Exception {
        final List<Long> expectedCommentsIds = List.of(1L, 2L);

        final List<Long> commentIds = commentRepository.findAllByItemId(3L).stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        assertEquals(expectedCommentsIds, commentIds, "CommentIds and expectedCommentsIds is not match");
    }
}
