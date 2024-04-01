package ru.practicum.shareit.booking;

import java.util.List;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.headers.WithUserHeaderID;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController  implements WithUserHeaderID {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDto add(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        log.info("Request to add user {} booking {}", userId, bookingDto);

        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingResponseDto update(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam(name = "approved") Boolean approved) {
        log.info("Request to update user {} approve {} booking {}", userId, approved, bookingId);

        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingResponseDto findByUserIdAndItemId(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable("bookingId") Long bookingId) {
        log.info("Request to load user {} booking with id {}", userId, bookingId);

        return bookingService.findByUserIdAndItemId(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findAllByUserIdAndItemId(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Request to load user {} bookings in state {}", userId, state);

        return bookingService.findAllByUserIdAndItemId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingListByItemOwner(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Request to load user {} bookings items in state {}", userId, state);

        return bookingService.getBookingListByItemOwner(userId, state);
    }
}
