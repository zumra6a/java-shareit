package ru.practicum.shareit.item.service;

import java.util.List;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

public interface ItemService {
    ItemResponseDto add(Long userId, ItemDto itemDto);

    ItemResponseDto update(Long userId, Long itemId, ItemDto itemDto);

    List<ItemResponseDto> findAllByUserId(Long userId, Integer from, Integer size);

    List<ItemResponseDto> search(Long userId, String text, Integer from, Integer size);

    ItemResponseDto findOneById(Long userId, Long itemId);

    CommentResponseDto addComment(Long userId, CommentDto commentDto, Long itemId);
}
