package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
public class BookingResponseDto {
    private Long id;

    private ItemResponseDto item;

    private UserDto booker;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
