package ru.practicum.shareit.item.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class ItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private ItemBookingResponseDto lastBooking;

    private ItemBookingResponseDto nextBooking;

    private List<CommentResponseDto> comments;
}
