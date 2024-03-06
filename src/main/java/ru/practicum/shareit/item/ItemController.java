package ru.practicum.shareit.item;

import java.util.List;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto item) {
        log.info("Request to add user {} item {}", userId, item);

        item.setOwner(userId);

        return itemService.add(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto item,
            @PathVariable("itemId") Long itemId) {
        log.info("Request to update user {} item {} with id {}", userId, item, itemId);

        item.setOwner(userId);
        item.setId(itemId);

        return itemService.update(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto findOneById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId) {
        log.info("Request to load user {} item with id {}", userId, itemId);

        return itemService.findOneById(itemId);
    }

    @GetMapping
    public List<ItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request to load user {} items", userId);

        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        log.info("Request to search items with text {}", text);

        return itemService.search(text);
    }
}
