package ru.practicum.shareit.request;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.headers.WithUserHeaderID;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController implements WithUserHeaderID {
    private final ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.add(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequesterId(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemRequestClient.findAllByRequesterId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long requestId) {
        return itemRequestClient.findById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllById(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemRequestClient.findAllById(userId, from, size);
    }
}
