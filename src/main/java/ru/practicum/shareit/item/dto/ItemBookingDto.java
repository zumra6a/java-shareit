package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemBookingDto {
    private Long id;
    private Long bookerId;
}
