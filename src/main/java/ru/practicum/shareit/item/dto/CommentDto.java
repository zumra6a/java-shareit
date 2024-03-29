package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import ru.practicum.shareit.validation.Marker;

@Data
public class CommentDto {
    @NotBlank(groups = {Marker.OnCreate.class})
    private String text;
}
