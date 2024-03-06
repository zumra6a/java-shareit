package ru.practicum.shareit.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true, access = AccessLevel.PUBLIC)
public class User {
    private Long id;

    private String name;

    @NotNull(message = "User email should not be null")
    @Email(message = "User email should have well-formed email address")
    @NotEmpty(message = "User email should not be empty")
    private String email;
}
