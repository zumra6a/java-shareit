package ru.practicum.shareit.booking.service;

import java.util.List;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

public interface BookingService {
    BookingResponseDto add(Long userId, BookingDto bookingDto);

    BookingResponseDto update(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto findByUserIdAndItemId(Long userId, Long bookingId);

    List<BookingResponseDto> findAllByUserIdAndItemId(Long userId, String state);

    List<BookingResponseDto> getBookingListByItemOwner(Long userId, String state);
}
