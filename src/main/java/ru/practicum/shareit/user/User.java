package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true, access = AccessLevel.PUBLIC)
public class User {
    private Long id;

    private String name;

    private String email;
}
