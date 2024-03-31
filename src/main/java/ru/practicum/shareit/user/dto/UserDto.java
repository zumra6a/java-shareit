package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Marker;

@Builder
@Data
public class UserDto {
    private Long id;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 255)
    private String name;

    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 512)
    private String email;
}
