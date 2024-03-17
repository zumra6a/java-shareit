package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Marker;

@Builder
@Data
public class UserDto {
    private Long id;

    @NotBlank(groups = {Marker.OnCreate.class})
    private String name;

    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotBlank(groups = {Marker.OnCreate.class})
    private String email;
}
