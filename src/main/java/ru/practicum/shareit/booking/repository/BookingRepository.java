package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime before);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime before);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime before);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerOrderByStartDesc(User itemOwner);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
            User itemOwner,
            LocalDateTime start,
            LocalDateTime before);

    List<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(User itemOwner, LocalDateTime before);

    List<Booking> findAllByItemOwnerAndStartAfterOrderByStartDesc(User itemOwner, LocalDateTime before);

    List<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(User itemOwner, BookingStatus status);

    Booking findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
            Long itemId,
            Long bookerId,
            LocalDateTime end,
            BookingStatus status);

    Booking findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId,
            Long bookerId,
            LocalDateTime start,
            BookingStatus status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndEndBefore(
            Long itemId,
            Long bookerId,
            LocalDateTime end);
}
