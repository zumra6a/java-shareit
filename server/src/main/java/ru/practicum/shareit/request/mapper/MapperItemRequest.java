package ru.practicum.shareit.request.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

@UtilityClass
public class MapperItemRequest {
    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

    public static ItemRequestResponseDto toRequestResponseDto(ItemRequest request) {
        List<ItemResponseDto> items = new ArrayList<>();

        if (!Objects.isNull(request.getItems())) {
            items = request.getItems().stream()
                    .map(MapperItem::toItemResponseDto)
                    .collect(Collectors.toList());
        }

        return ItemRequestResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }
}
