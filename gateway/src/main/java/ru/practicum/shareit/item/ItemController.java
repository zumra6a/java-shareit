package ru.practicum.shareit.item;

import javax.validation.constraints.Min;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.headers.WithUserHeaderID;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Marker;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
public class ItemController implements WithUserHeaderID {
    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Validated(Marker.OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Request to add user {} item {}", userId, itemDto);

        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Validated(Marker.OnUpdate.class) @RequestBody ItemDto itemDto,
            @PathVariable("itemId") Long itemId) {
        log.info("Request to update user {} item {} with id {}", userId, itemDto, itemId);

        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findOneById(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable("itemId") Long itemId) {
        log.info("Request to load user {} item with id {}", userId, itemId);

        return itemClient.findOneById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("Request to load user {} items", userId);

        return itemClient.findAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(name = "text") String text,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("Request to search items with text {}", text);

        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Validated(Marker.OnCreate.class) @RequestBody CommentDto commentDto,
            @PathVariable("itemId") Long itemId) {
        log.info("Request to add user {} comment {} to item with id {}", userId, commentDto, itemId);

        return itemClient.addComment(userId, commentDto, itemId);
    }
}
