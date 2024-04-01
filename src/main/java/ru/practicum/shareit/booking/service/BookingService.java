package ru.practicum.shareit.booking.service;

import java.util.List;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

public interface BookingService {
    BookingResponseDto add(Long userId, BookingDto bookingDto);

    BookingResponseDto update(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto findByUserIdAndBookingId(Long userId, Long bookingId);

    List<BookingResponseDto> findAllByBookerId(Long userId, String state, Integer from, Integer size);

    List<BookingResponseDto> findAllByOwnerId(Long userId, String state, Integer from, Integer size);
}
