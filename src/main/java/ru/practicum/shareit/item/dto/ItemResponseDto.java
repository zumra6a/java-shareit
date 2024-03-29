package ru.practicum.shareit.item.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Builder(toBuilder = true)
@Data
public class ItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private ItemBookingResponseDto lastBooking;

    private ItemBookingResponseDto nextBooking;

    @Singular
    private List<CommentResponseDto> comments;
}
