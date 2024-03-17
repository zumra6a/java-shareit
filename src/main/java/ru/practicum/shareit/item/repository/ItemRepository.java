package ru.practicum.shareit.item.repository;

import java.util.List;

import ru.practicum.shareit.item.model.Item;

public interface ItemRepository {
    Item add(Item item);

    Item update(Item item);

    List<Item> findAllByUserId(Long userId);

    List<Item> search(String text);

    Item findOneById(Long itemId);
}
