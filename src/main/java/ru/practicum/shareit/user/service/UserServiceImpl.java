package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapperUser;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
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
        final User user = userRepository.findOneById(userId);

        return MapperUser.toUserDto(user);
    }

    @Override
    public UserDto add(UserDto user) {
        return MapperUser.toUserDto(userRepository.add(MapperUser.toUser(user)));
    }

    @Override
    public UserDto update(UserDto user) {
        return MapperUser.toUserDto(userRepository.update(MapperUser.toUser(user)));
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }
}
