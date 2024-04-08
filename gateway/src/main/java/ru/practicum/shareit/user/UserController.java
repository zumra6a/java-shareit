package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Marker;

@Slf4j
@Validated
@Controller
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Request to load all users");

        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findOneById(@PathVariable("userId") Long userId) {
        log.info("Request to load user with id {}", userId);

        return userClient.findOneById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@Validated(Marker.OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Request to add user {}", userDto);

        return userClient.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(
            @PathVariable("userId") Long userId,
            @Validated(Marker.OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("Request to update user {} with id {}", userDto, userId);

        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") Long userId) {
        log.info("Request to delete user with id {}", userId);

        return userClient.deleteById(userId);
    }
}
