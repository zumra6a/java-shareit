package ru.practicum.shareit.item.service;

import java.util.List;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
    ItemDto add(ItemDto item);

    ItemDto update(ItemDto item);

    List<ItemDto> findAllByUserId(Long userId);

    List<ItemDto> search(String text);

    ItemDto findOneById(Long itemId);
}
