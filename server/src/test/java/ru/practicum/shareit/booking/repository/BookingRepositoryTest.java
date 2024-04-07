package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User user = User.builder()
            .name("user")
            .email("user@email.com")
            .build();

    private final User owner = User.builder()
            .name("owner")
            .email("owner@email.com")
            .build();

    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .owner(owner)
            .build();

    private final Booking booking = Booking.builder()
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusHours(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .build();

    private final Booking pastBooking = Booking.builder()
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(2L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    private final Booking futureBooking = Booking.builder()
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    @BeforeEach
    void setUp() {
        testEntityManager.persist(user);
        testEntityManager.persist(owner);
        testEntityManager.persist(item);
        testEntityManager.flush();
        bookingRepository.save(booking);
        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerId() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(1L, PageRequest.of(0, 10));

        assertEquals(bookings.size(), 3);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void findAllByBookerIdAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBefore(
                1L,
                LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), 2L);
    }

    @Test
    void findAllByBookerIdAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfter(
                1L,
                LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), 3L);
    }

    @Test
    void findAllByBookerIdAndStatus() {
        Booking waitingBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(waitingBooking);

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(
                1L,
                BookingStatus.WAITING,
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), BookingStatus.WAITING);
    }

    @Test
    void findAllByItemOwner() {
        List<Booking> bookings = bookingRepository.findAllByItemOwner(owner, PageRequest.of(0, 10));

        assertEquals(bookings.size(), 3);
    }

    @Test
    void findAllByItemOwnerAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(
                owner,
                LocalDateTime.now(),
                LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 2L);
    }

    @Test
    void findAllByItemOwnerAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndEndBefore(
                owner,
                LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 2L);
    }

    @Test
    void findAllByItemOwnerAndStartAfter() {
        Booking waitingBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(waitingBooking);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStartAfter(
                owner,
                LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(1).getStatus(), BookingStatus.WAITING);
    }

    @Test
    void findAllByItemOwnerAndStatus() {
        Booking rejectedBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(rejectedBooking);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStatus(
                owner,
                BookingStatus.REJECTED,
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatus() {
        Booking actual = bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatus(
                1L,
                2L,
                LocalDateTime.now(),
                BookingStatus.APPROVED,
                Sort.by(Sort.Direction.DESC, "start"));

        assertEquals(actual.getId(), 1L);
    }

    @Test
    void findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatus() {
        Booking actual = bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatus(
                1L,
                2L,
                LocalDateTime.now(),
                BookingStatus.APPROVED,
                Sort.by(Sort.Direction.ASC, "start"));

        assertEquals(actual.getId(), 3L);
    }

    @Test
    void findFirstByBookerIdAndItemIdAndEndBefore() {
        Optional<Booking> actualOpt = bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(
                1L,
                1L,
                LocalDateTime.now());

        assertEquals(actualOpt.isPresent(), true);

        Booking actual = actualOpt.get();

        assertEquals(actual.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void findAllByItemInAndStatus() {
        List<Booking> actual = bookingRepository.findAllByItemInAndStatus(
                List.of(item),
                BookingStatus.APPROVED,
                PageRequest.of(0, 10));

        assertEquals(actual.size(), 3);
        assertEquals(actual.get(1).getStatus(), BookingStatus.APPROVED);
    }
}
