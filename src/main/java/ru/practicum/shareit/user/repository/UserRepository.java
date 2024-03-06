package ru.practicum.shareit.user.repository;

import java.util.List;

import ru.practicum.shareit.user.User;

public interface UserRepository {
    List<User> findAll();

    User findOneById(Long userId);

    User add(User user);

    User update(User user);

    void deleteById(Long userId);
}
