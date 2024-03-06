package ru.practicum.shareit.user.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotUniqueElementException;
import ru.practicum.shareit.user.User;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static Long id = 1L;
    private final Map<Long, User> users = new HashMap<>();

    private final Set<String> emails = new HashSet<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findOneById(Long userId) {
        validateUserById(userId);

        return users.get(userId);
    }

    @Override
    public User add(User user) {
        validateEmail(user.getEmail());
        user.setId(id);
        users.put(id, user);
        emails.add(user.getEmail());
        id++;

        return user;
    }

    @Override
    public User update(User user) {
        long userId = user.getId();

        validateUserById(userId);

        User oldUser = users.get(userId);
        if (Objects.isNull(user.getEmail())) {
            user.setEmail(oldUser.getEmail());
        }

        if (Objects.isNull(user.getName())) {
            user.setName(oldUser.getName());
        }

        updateEmail(oldUser.getEmail(), user.getEmail());
        users.put(userId, user);

        return users.get(userId);
    }

    @Override
    public void deleteById(Long userId) {
        validateUserById(userId);
        emails.remove(findOneById(userId).getEmail());
        users.remove(userId);
    }

    private void validateUserById(Long userId) {
        System.out.println("users = " + users);
        if (!users.containsKey(userId)) {
            throw new NoSuchElementException(String.format("User with id %d not found", id));
        }
    }

    private void validateEmail(String email) {
        if (emails.contains(email)) {
            throw new NotUniqueElementException(String.format("User email %s is not unique", email));
        }
    }

    private void updateEmail(String oldEmail, String newEmail) {
        if (oldEmail.equals(newEmail)) {
            return;
        }

        if (emails.contains(newEmail)) {
            throw new NotUniqueElementException(String.format("User email %s is not unique", newEmail));
        }

        emails.remove(oldEmail);
        emails.add(newEmail);
    }
}
