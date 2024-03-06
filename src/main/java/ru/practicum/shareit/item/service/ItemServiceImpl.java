package ru.practicum.shareit.item.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.item.repository.ItemRepository;

@Service
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto add(ItemDto item) {
        return MapperItem.toItemDto(itemRepository.add(MapperItem.toItem(item)));
    }

    @Override
    public ItemDto update(ItemDto item) {
        return MapperItem.toItemDto(itemRepository.update(MapperItem.toItem(item)));
    }

    @Override
    public ItemDto findOneById(Long itemId) {
        return MapperItem.toItemDto(itemRepository.findOneById(itemId));
    }

    @Override
    public List<ItemDto> findAllByUserId(Long userId) {
        return itemRepository.findAllByUserId(userId).stream()
                .map(MapperItem::toItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(MapperItem::toItemDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
