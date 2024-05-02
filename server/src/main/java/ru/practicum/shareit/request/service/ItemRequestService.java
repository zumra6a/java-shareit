package ru.practicum.shareit.request.service;

import java.util.List;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

public interface ItemRequestService {
    ItemRequestResponseDto add(Long userId, ItemRequestDto requestDto);

    List<ItemRequestResponseDto> findAllByRequesterId(Long userId);

    ItemRequestResponseDto findById(Long userId, Long requestId);

    List<ItemRequestResponseDto> findAllById(Long userId, Integer from, Integer size);
}
