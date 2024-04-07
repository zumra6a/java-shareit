package ru.practicum.shareit.item.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User user = User.builder()
            .name("name")
            .email("email@email.com")
            .build();
    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final Comment comment = Comment.builder()
            .item(item)
            .author(user)
            .created(LocalDateTime.now())
            .text("comment")
            .build();

    private final Item item2 = item.toBuilder()
            .name("other name")
            .description("other description")
            .build();

    private final Comment comment2 = comment.builder()
            .item(item2)
            .author(user)
            .text("other comment")
            .build();

    @BeforeEach
    void setUp() {
        testEntityManager.persist(user);
        testEntityManager.persist(item);
        testEntityManager.persist(item2);
        testEntityManager.flush();
        commentRepository.save(comment);
        commentRepository.save(comment2);
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
    }

    @Test
    void findByItemId() {
        List<Comment> comments = commentRepository.findByItemId(1L);

        assertEquals(comments.size(), 1);
        assertEquals(comments.get(0).getText(), "comment");
    }

    @Test
    void findAllByItemIdIn() {
        List<Comment> comments = commentRepository.findAllByItemIdIn(List.of(1L, 2L));

        assertEquals(comments.size(), 2);
        assertEquals(comments.get(0).getText(), "comment");
        assertEquals(comments.get(1).getText(), "other comment");
    }
}
