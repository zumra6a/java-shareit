package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long owner;

    private Long requestId;
}
