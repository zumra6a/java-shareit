package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapperUser;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("email@email.com")
            .build();
    private final UserDto userDto = MapperUser.toUserDto(user);

    @Test
    void findAll() {
        List<User> users = List.of(user);

        List<UserDto> expected = users.stream()
                .map(MapperUser::toUserDto)
                .collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> actual = userService.findAll();

        assertEquals(actual.size(), 1);
        assertEquals(actual, expected);
    }

    @Test
    void findOneById() {
        Long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserDto expected = MapperUser.toUserDto(user);
        UserDto actual = userService.findOneById(userId);

        assertEquals(expected, actual);
    }

    @Test
    void findUnknownUserOneById() {
        Long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> userService.findOneById(userId));

        assertEquals(exception.getMessage(), "User with id 1 not found");
    }

    @Test
    void add() {
        when(userRepository.save(user))
                .thenReturn(user);

        UserDto actual = userService.add(userDto);

        assertEquals(userDto, actual);
        verify(userRepository).save(user);
    }

    @Test
    void update() {
        User existingUser = user.toBuilder()
                .build();

        UserDto updatedUser = UserDto.builder()
                .email("update@email.com")
                .name("updated name")
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(existingUser));

        UserDto actual = userService.update(existingUser.getId(), updatedUser);

        assertNotNull(actual);
        assertEquals("updated name", actual.getName());
        assertEquals("update@email.com", actual.getEmail());
    }

    @Test
    void deleteById() {
        Long userId = 4L;
        userService.deleteById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
