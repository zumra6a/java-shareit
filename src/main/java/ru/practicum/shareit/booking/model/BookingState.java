package ru.practicum.shareit.booking.model;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    WAITING,
    REJECTED,
    FUTURE;

    public static Optional<BookingState> toEnum(String state) {
        try {
            return Optional.of(BookingState.valueOf(state));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
