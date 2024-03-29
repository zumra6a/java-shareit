package ru.practicum.shareit.item;

import java.util.List;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Marker;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ItemResponseDto add(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        log.info("Request to add user {} item {}", userId, itemDto);

        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Validated(Marker.OnUpdate.class)
    public ItemResponseDto update(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Valid @RequestBody ItemDto itemDto,
            @PathVariable("itemId") Long itemId) {
        log.info("Request to update user {} item {} with id {}", userId, itemDto, itemId);

        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto findOneById(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable("itemId") Long itemId) {
        log.info("Request to load user {} item with id {}", userId, itemId);

        return itemService.findOneById(userId, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> findAllByUserId(@RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Request to load user {} items", userId);

        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(name = "text") String text) {
        log.info("Request to search items with text {}", text);

        return itemService.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    @Validated(Marker.OnCreate.class)
    public CommentResponseDto addComment(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Valid @RequestBody CommentDto commentDto,
            @PathVariable("itemId") Long itemId) {
        log.info("Request to add user {} comment {} to item with id {}", userId, commentDto, itemId);

        return itemService.addComment(userId, commentDto, itemId);
    }
}
