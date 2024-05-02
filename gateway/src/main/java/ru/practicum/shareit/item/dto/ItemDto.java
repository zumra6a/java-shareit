package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Marker;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemDto {
    private Long id;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 255)
    private String name;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 512)
    private String description;

    @NotNull(groups = {Marker.OnCreate.class})
    private Boolean available;

    private Long requestId;
}
