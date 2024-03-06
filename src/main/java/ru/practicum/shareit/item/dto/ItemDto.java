package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Marker;

@Builder
@Data
public class ItemDto {
    private Long id;

    @NotBlank(groups = {Marker.OnCreate.class})
    private String name;

    @NotBlank(groups = {Marker.OnCreate.class})
    private String description;

    @NotNull(groups = {Marker.OnCreate.class})
    private Boolean available;

    private Long owner;
}
