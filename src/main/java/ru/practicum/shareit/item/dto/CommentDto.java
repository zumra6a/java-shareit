package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import ru.practicum.shareit.validation.Marker;

@Data
public class CommentDto {
    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 255)
    private String text;
}
