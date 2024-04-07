package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class MapperItem {
    public ItemDto toItemDto(Item item) {
        Long requestId = null;

        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .owner(item.getOwner().getId())
                .requestId(requestId)
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemResponseDto toItemResponseDto(Item item) {
        Long requestId = null;

        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        }

        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(requestId)
                .build();
    }
}
