package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.headers.WithUserHeaderID;
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.MapperUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest implements WithUserHeaderID {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("email@adress.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("description")
            .owner(user)
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.WAITING)
            .booker(MapperUser.toUserDto(user))
            .item(MapperItem.toItemResponseDto(item))
            .build();

    @Test
    @SneakyThrows
    void shouldAddBooking() {
        when(bookingService.add(user.getId(), bookingDto))
                .thenReturn(bookingResponseDto);

        String result = mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header(HEADER_USER_ID, user.getId())
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @Test
    @SneakyThrows
    void shouldUpdateBooking() {
        Boolean approved = true;
        Long bookingId = 1L;

        when(bookingService.update(user.getId(), bookingId, approved))
                .thenReturn(bookingResponseDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .contentType("application/json")
                        .header(HEADER_USER_ID, user.getId())
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @Test
    @SneakyThrows
    void shouldFindAllBookingsByUserId() {
        Integer from = 0;
        Integer size = 10;
        String state = "ALL";

        when(bookingService.findAllByBookerId(user.getId(), BookingState.ALL.toString(), 0, 10))
                .thenReturn(List.of(bookingResponseDto));

        String result = mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER_USER_ID, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingResponseDto)), result);
    }

    @Test
    @SneakyThrows
    void shouldFindBookingByUserIdAndBookingId() {
        Long bookingId = 1L;

        when(bookingService.findByUserIdAndBookingId(user.getId(), bookingId))
                .thenReturn(bookingResponseDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(HEADER_USER_ID, user.getId())).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @Test
    @SneakyThrows
    void shouldFindAllByOwnerId() {
        Integer from = 0;
        Integer size = 10;
        String state = "ALL";

        when(bookingService.findAllByOwnerId(user.getId(), BookingState.ALL.toString(), 0, 10))
                .thenReturn(List.of(bookingResponseDto));

        String result = mockMvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(HEADER_USER_ID, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingResponseDto)), result);
    }
}
