package ru.practicum.shareit.request.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository requestRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User user1 = User.builder()
            .name("name")
            .email("email@email.com")
            .build();

    private final User user2 = User.builder()
            .name("name2")
            .email("email2@email.com")
            .build();

    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .owner(user1)
            .build();

    private final ItemRequest request1 = ItemRequest.builder()
            .items(List.of(item))
            .description("request description")
            .created(LocalDateTime.now())
            .requester(user1)
            .build();

    private final ItemRequest request2 = ItemRequest.builder()
            .items(List.of(item))
            .description("other description")
            .created(LocalDateTime.now())
            .requester(user2)
            .build();

    @BeforeEach
    void setUp() {
        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.persist(item);
        testEntityManager.flush();
        requestRepository.save(request1);
        requestRepository.save(request2);
    }

    @AfterEach
    void tearDown() {
        requestRepository.deleteAll();
    }

    @Test
    void findAllByRequesterId() {
        List<ItemRequest> requests = requestRepository.findAllByRequesterId(1L);

        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getDescription(), "request description");
    }

    @Test
    void findAllByRequester_IdNot() {
        List<ItemRequest> requests = requestRepository.findAllByRequester_IdNot(1L, PageRequest.ofSize(5));

        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getDescription(), "other description");
    }
}
