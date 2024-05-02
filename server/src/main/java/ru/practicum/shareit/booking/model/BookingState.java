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
        for (BookingState bookingState : BookingState.values()) {
            if (bookingState.toString().equalsIgnoreCase(state)) {
                return Optional.of(BookingState.valueOf(state));
            }
        }
        return Optional.empty();
    }
}
