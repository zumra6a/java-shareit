package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.MapperBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapperUser;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("email@adress.com")
            .build();

    private final UserDto userDto = MapperUser.toUserDto(user);

    private final Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.WAITING)
            .item(item)
            .booker(user)
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final BookingDto bookingDtoEndBeforeStart = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    @Test
    void createWhenEndIsBeforeStartShouldThrowValidationException() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> bookingService.add(userDto.getId(), bookingDtoEndBeforeStart));

        assertEquals(exception.getMessage(), "User 1 can not book own item 1");
    }

    @Test
    void createWhenItemIsNotAvailableShouldThrowValidationException() {
        Item unaviableItem = item.toBuilder()
                .available(false)
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(unaviableItem));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookingService.add(userDto.getId(), bookingDto));

        assertEquals(exception.getMessage(), "Item 1 is unavailable");
    }

    @Test
    void createWhenItemOwnerEqualsBookerShouldThrowValidationException() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> bookingService.add(userDto.getId(), bookingDto));

        assertEquals(exception.getMessage(), "User 1 can not book own item 1");
    }

    @Test
    void update() {
        Booking bookingWaiting = booking.toBuilder()
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingWaiting.getId()))
                .thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(bookingWaiting))
                .thenReturn(bookingWaiting);

        BookingResponseDto actual = bookingService.update(
                user.getId(),
                bookingWaiting.getId(),
                true);

        assertEquals(BookingStatus.APPROVED, actual.getStatus());
    }

    @Test
    void updateWhenStatusNotApproved() {
        Booking bookingWaiting = booking.toBuilder()
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingWaiting.getId()))
                .thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(bookingWaiting))
                .thenReturn(bookingWaiting);

        BookingResponseDto actual = bookingService.update(
                user.getId(),
                bookingWaiting.getId(),
                false);

        assertEquals(BookingStatus.REJECTED, actual.getStatus());
    }

    @Test
    void updateShouldStatusNotWaiting() {
        Booking bookingApproved = booking.toBuilder()
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingApproved.getId()))
                .thenReturn(Optional.of(bookingApproved));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookingService.update(user.getId(), booking.getId(), false));

        assertEquals(exception.getMessage(), "Booking is not in available status");
    }

    @Test
    void updateWhenUserIsNotItemOwnerShouldThrowNoSuchElementException() {
        User newUser = user.toBuilder()
                .id(2L)
                .build();
        Item newItem = item.toBuilder()
                .id(2L)
                .owner(newUser)
                .build();
        Booking newBooking = booking.toBuilder()
                .item(newItem)
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(newBooking.getId()))
                .thenReturn(Optional.of(newBooking));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> bookingService.update(user.getId(), newBooking.getId(), true));

        assertEquals(exception.getMessage(), "User 1 does not own item 2");
    }

    @Test
    void getById() {
        User newUser = user.toBuilder()
                .id(2L)
                .build();
        Booking newBooking = booking.toBuilder()
                .booker(newUser)
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(newBooking.getId()))
                .thenReturn(Optional.of(newBooking));

        BookingResponseDto expected = MapperBooking.toBookingResponseDto(newBooking);
        BookingResponseDto actual = bookingService.findByUserIdAndBookingId(user.getId(), newBooking.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getByIdWhenBookingIdIsNotValidShouldThrowObjectNoSuchElementException() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> bookingService.findByUserIdAndBookingId(user.getId(), booking.getId()));

        assertEquals(exception.getMessage(), "Booking 1 not found");
    }

    @Test
    void getByIdWhenUserIsNotItemOwnerShouldThrowObjectNoSuchElementException() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> bookingService.findByUserIdAndBookingId(user.getId(), booking.getId()));

        assertEquals(exception.getMessage(), "User 1 does not own or booked item 1");
    }

    @Test
    void getAllByBookerWhenBookingStateAll() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByBookerId(user.getId(), "ALL", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByBooker_whenBookingStateCURRENT() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByBookerId(user.getId(), "CURRENT", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByBookerWhenBookingStatePAST() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndEndBefore(
                anyLong(),
                any(LocalDateTime.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByBookerId(user.getId(), "PAST", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByBookerWhenBookingStateFUTURE() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartAfter(
                anyLong(),
                any(LocalDateTime.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByBookerId(user.getId(), "FUTURE", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByBookerWhenBookingStateWAITING() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByBookerId(user.getId(), "WAITING", 0, 10);

        assertEquals(expected, actual);
    }


    @Test
    void getAllByBookerWhenBookingStateREJECTED() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByBookerId(user.getId(), "REJECTED", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByBookerWhenBookingStateIsNotValidShouldThrowIllegalArgumentException() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class,
                () -> bookingService.findAllByBookerId(user.getId(), "UNKNOWN", 0, 10));
    }

    @Test
    void getAllByOwnerWhenBookingStateAll() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwner(
                any(User.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByOwnerId(user.getId(), "ALL", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByOwnerWhenBookingStateCURRENT() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(
                any(User.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByOwnerId(user.getId(), "CURRENT", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByOwnerWhenBookingStatePAST() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndEndBefore(
                any(User.class),
                any(LocalDateTime.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByOwnerId(user.getId(), "PAST", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByOwnerWhenBookingStateFUTURE() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStartAfter(
                any(User.class),
                any(LocalDateTime.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByOwnerId(user.getId(), "FUTURE", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByOwnerWhenBookingStateWAITING() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStatus(
                any(User.class),
                any(BookingStatus.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByOwnerId(user.getId(), "WAITING", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByOwnerWhenBookingStateREJECTED() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStatus(
                any(User.class),
                any(BookingStatus.class),
                any(Pageable.class))
        ).thenReturn(List.of(booking));

        List<BookingResponseDto> expected = List.of(MapperBooking.toBookingResponseDto(booking));
        List<BookingResponseDto> actual = bookingService.findAllByOwnerId(user.getId(), "REJECTED", 0, 10);

        assertEquals(expected, actual);
    }

    @Test
    void getAllByOwnerWhenBookingStateIsNotValidThenThrowIllegalStateException() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class,
                () -> bookingService.findAllByOwnerId(user.getId(), "UNKNOWN", 0, 10));
    }
}
