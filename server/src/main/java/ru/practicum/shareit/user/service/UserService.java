package ru.practicum.shareit.user.service;

import java.util.List;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    List<UserDto> findAll();

    UserDto findOneById(long userId);

    UserDto add(UserDto user);

    UserDto update(Long userId, UserDto user);

    void deleteById(Long userId);
}
