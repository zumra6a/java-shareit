package ru.practicum.shareit.request.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.MapperItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    @Autowired
    public ItemRequestServiceImpl(UserRepository userRepository, ItemRequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional
    public ItemRequestResponseDto add(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));

        ItemRequest request = MapperItemRequest.toItemRequest(itemRequestDto);

        request.setRequester(user);

        return MapperItemRequest.toRequestResponseDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestResponseDto> findAllByRequesterId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));

        List<ItemRequest> itemRequestList = requestRepository.findAllByRequesterId(userId);

        return itemRequestList.stream()
                .map(MapperItemRequest::toRequestResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto findById(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Request %d not found", requestId)));

        return MapperItemRequest.toRequestResponseDto(request);
    }

    @Override
    public List<ItemRequestResponseDto> findAllById(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));

        List<ItemRequest> itemRequests = requestRepository.findAllByRequester_IdNot(
                userId,
                PageRequest.of(
                        from / size,
                        size,
                        Sort.by(Sort.Direction.ASC, "created")));

        return itemRequests.stream()
                .map(MapperItemRequest::toRequestResponseDto)
                .collect(Collectors.toList());
    }
}
