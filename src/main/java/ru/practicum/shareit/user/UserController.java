package ru.practicum.shareit.user;

import java.util.List;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Marker;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Request to load all users");

        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findOneById(@PathVariable("userId") Long userId) {
        log.info("Request to load user with id {}", userId);

        return userService.findOneById(userId);
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public UserDto add(@Valid @RequestBody UserDto user) {
        log.info("Request to add user {}", user);

        return userService.add(user);
    }

    @PatchMapping("/{userId}")
    @Validated(Marker.OnUpdate.class)
    public UserDto update(@PathVariable("userId") Long userId, @Valid @RequestBody UserDto user) {
        log.info("Request to update user {} with id {}", user, userId);

        return userService.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        log.info("Request to delete user with id {}", userId);

        userService.deleteById(userId);
    }
}
