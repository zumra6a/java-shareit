package ru.practicum.shareit.booking.dto;


import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class BookingDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
