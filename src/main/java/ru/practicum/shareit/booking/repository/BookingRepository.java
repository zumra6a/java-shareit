package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime before,
            Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime before, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime before, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwner(User itemOwner, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(
            User itemOwner,
            LocalDateTime start,
            LocalDateTime before,
            Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(User itemOwner, LocalDateTime before, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User itemOwner, LocalDateTime before, Sort sort);

    List<Booking> findAllByItemOwnerAndStatus(User itemOwner, BookingStatus status, Sort sort);

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
}
