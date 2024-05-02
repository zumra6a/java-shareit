package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private final UserDto userDto1 = UserDto.builder()
            .name("name1")
            .email("email1@email.com")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("name2")
            .email("email2@email.com")
            .build();

    private final ItemDto itemDto1 = ItemDto.builder()
            .name("item1 name")
            .description("item1 description")
            .available(true)
            .build();

    private final ItemDto itemDto2 = ItemDto.builder()
            .name("item2 name")
            .description("item2 description")
            .available(true)
            .build();

    private final BookingDto bookingDto1 = BookingDto.builder()
            .itemId(2L)
            .start(LocalDateTime.now().plusSeconds(10L))
            .end(LocalDateTime.now().plusSeconds(11L))
            .build();

    @Test
    void bookingOperations() {
        UserDto addedUser1 = userService.add(userDto1);
        UserDto addedUser2 = userService.add(userDto2);
        itemService.add(addedUser1.getId(), itemDto1);
        itemService.add(addedUser2.getId(), itemDto2);

        BookingResponseDto bookingResponseDto1 = bookingService.add(addedUser1.getId(), bookingDto1);
        BookingResponseDto bookingResponseDto2 = bookingService.add(addedUser1.getId(), bookingDto1);

        assertEquals(1L, bookingResponseDto1.getId());
        assertEquals(2L, bookingResponseDto2.getId());
        assertEquals(BookingStatus.WAITING, bookingResponseDto1.getStatus());
        assertEquals(BookingStatus.WAITING, bookingResponseDto2.getStatus());

        BookingResponseDto updatedBookingDto1 = bookingService.update(addedUser2.getId(),
                bookingResponseDto1.getId(), true);
        BookingResponseDto updatedBookingDto2 = bookingService.update(addedUser2.getId(),
                bookingResponseDto2.getId(), true);

        assertEquals(BookingStatus.APPROVED, updatedBookingDto1.getStatus());
        assertEquals(BookingStatus.APPROVED, updatedBookingDto2.getStatus());

        List<BookingResponseDto> bookingsDtoOut = bookingService.findAllByOwnerId(
                addedUser2.getId(),
                "ALL",
                0,
                10);

        assertEquals(2, bookingsDtoOut.size());
    }

    @Test
    void throwNoSuchElementExceptionOnInvalidUserOrBooking() {
        Long userId = 3L;
        Long bookingId = 3L;

        assertThrows(NoSuchElementException.class,
                        () -> bookingService.update(userId, bookingId, true));
    }
}
