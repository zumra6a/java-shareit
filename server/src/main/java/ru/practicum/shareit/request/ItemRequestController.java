package ru.practicum.shareit.request;

import java.util.List;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController implements WithUserHeaderID {
    private final ItemRequestService requestService;

    @Autowired
    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestResponseDto add(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.add(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> findAllByRequesterId(@RequestHeader(HEADER_USER_ID) Long userId) {
        return requestService.findAllByRequesterId(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findById(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long requestId) {
        return requestService.findById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> findAllById(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return requestService.findAllById(userId, from, size);
    }
}
