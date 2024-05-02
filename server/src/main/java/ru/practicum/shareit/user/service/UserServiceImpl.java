package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapperUser;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(MapperUser::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findOneById(long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User with id %d not found", userId)));

        return MapperUser.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = MapperUser.toUser(userDto);

        return MapperUser.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User with id %d not found", userId)));

        String email = userDto.getEmail();
        if (!Objects.isNull(email) && !email.isBlank()) {
            user.setEmail(email);
        }

        String name = userDto.getName();
        if (!Objects.isNull(name) && !name.isBlank()) {
            user.setName(name);
        }

        userRepository.save(user);

        return MapperUser.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }
}
