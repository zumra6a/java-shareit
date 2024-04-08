package ru.practicum.shareit.booking;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.headers.WithUserHeaderID;

@Slf4j
@Controller
@Validated
@RequestMapping(path = "/bookings")
public class BookingController  implements WithUserHeaderID {
    private final BookingClient bookingClient;

    @Autowired
    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Valid @RequestBody BookItemRequestDto bookingDto) {
        log.info("Request to add user {} booking {}", userId, bookingDto);

        return bookingClient.add(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> update(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam(name = "approved") Boolean approved) {
        log.info("Request to update user {} approve {} booking {}", userId, approved, bookingId);

        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> findByUserIdAndBookingId(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable("bookingId") Long bookingId) {
        log.info("Request to load user {} booking with id {}", userId, bookingId);

        return bookingClient.findByUserIdAndBookingId(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBookerId(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalStateException("Unknown state: " + stateParam));

        log.info("Request to load user {} bookings in state {}, from={}, size={}", userId, state, from, size);

        return bookingClient.findAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwnerId(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalStateException("Unknown state: " + stateParam));

        log.info("Request to load user {} bookings items in state {}, from={}, size={}", userId, state, from, size);

        return bookingClient.findAllByOwnerId(userId, state, from, size);
    }
}
