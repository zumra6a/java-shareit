package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime before,
            Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime before, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime before, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwner(User itemOwner, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(
            User itemOwner,
            LocalDateTime start,
            LocalDateTime before,
            Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndBefore(User itemOwner, LocalDateTime before, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartAfter(User itemOwner, LocalDateTime before, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStatus(User itemOwner, BookingStatus status, Pageable pageable);

    Booking findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatus(
            Long itemId,
            Long bookerId,
            LocalDateTime end,
            BookingStatus status,
            Sort sort);

    Booking findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatus(
            Long itemId,
            Long bookerId,
            LocalDateTime start,
            BookingStatus status,
            Sort sort);

    Optional<Booking> findFirstByBookerIdAndItemIdAndEndBefore(
            Long itemId,
            Long bookerId,
            LocalDateTime end);

    List<Booking> findAllByItemInAndStatus(List<Item> items, BookingStatus status, Pageable pageable);
}
