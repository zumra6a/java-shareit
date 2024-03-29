package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.MapperBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(
            BookingRepository bookingRepository,
            ItemRepository itemRepository,
            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BookingResponseDto add(Long userId, BookingDto bookingDto) {
        Long itemId = bookingDto.getItemId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Item %d not found", itemId)));

        if (!item.getAvailable()) {
            throw new IllegalStateException(String.format("Item %d is unavailable", itemId));
        }

        if (item.isOwnedBy(userId)) {
            throw new NoSuchElementException(String.format("User %d does not own item %d", userId, itemId));
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new IllegalStateException("Booking end date cannot be earlier booking start date");
        }

        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new IllegalStateException("Booking end date cannot be equal to booking start date");
        }

        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        bookingRepository.save(booking);

        return MapperBooking.toBookingResponseDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto update(Long userId, Long bookingId, Boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Booking %d not found", bookingId)));

        if (!booking.getItem().isOwnedBy(userId)) {
            Long itemId = booking.getItem().getId();
            throw new NoSuchElementException(String.format("User %d does not own item %d", userId, itemId));
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Booking is not in available status");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        bookingRepository.save(booking);

        return MapperBooking.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto findByUserIdAndItemId(Long userId, Long bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Booking %d not found", bookingId)));

        boolean ownedBy = booking.getItem().isOwnedBy(userId);
        boolean bookedBy = booking.getBooker().getId().equals(userId);
        boolean ownerOrBooker = ownedBy ^ bookedBy;

        if (!ownerOrBooker) {
            Long itemId = booking.getItem().getId();
            throw new NoSuchElementException(String.format("User %d does not own or booked item %d", userId, itemId));
        }

        return MapperBooking.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> findAllByUserIdAndItemId(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));
        BookingState bookingState = BookingState.toEnum(state)
                .orElseThrow(() -> new IllegalStateException(String.format("Unknown state: %s", state)));

        List<Booking> bookings = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookings.addAll(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now()));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        userId,
                        LocalDateTime.now()));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now()));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId,
                        BookingStatus.WAITING));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId,
                        BookingStatus.REJECTED));
                break;
        }

        return bookings.stream()
                .map(MapperBooking::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingListByItemOwner(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));
        BookingState bookingState = BookingState.toEnum(state)
                .orElseThrow(() -> new IllegalStateException(String.format("Unknown state: %s", state)));

        List<Booking> bookings = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookings.addAll(bookingRepository.findAllByItemOwnerOrderByStartDesc(user));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                        user,
                        LocalDateTime.now(),
                        LocalDateTime.now()));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(
                        user,
                        LocalDateTime.now()));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(
                        user,
                        LocalDateTime.now()));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(
                        user,
                        BookingStatus.WAITING));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(
                        user,
                        BookingStatus.REJECTED));
                break;
        }

        return bookings.stream()
                .map(MapperBooking::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}
