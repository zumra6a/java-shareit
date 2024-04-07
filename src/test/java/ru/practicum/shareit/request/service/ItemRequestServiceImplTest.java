package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.MapperItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("request description")
            .items(List.of(item))
            .build();

    @Test
    void add() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("description")
                .build();

        ItemRequestResponseDto expected = MapperItemRequest.toRequestResponseDto(request);
        ItemRequestResponseDto actual = requestService.add(user.getId(), requestDto);

        assertEquals(expected, actual);
    }

    @Test
    void findAllByRequesterId() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterId(user.getId()))
                .thenReturn(List.of(request));

        List<ItemRequestResponseDto> expected = List.of(MapperItemRequest.toRequestResponseDto(request));
        List<ItemRequestResponseDto> actual = requestService.findAllByRequesterId(user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        ItemRequestResponseDto expected = MapperItemRequest.toRequestResponseDto(request);
        ItemRequestResponseDto actual = requestService.findById(user.getId(), request.getId());

        assertEquals(expected, actual);
    }

    @Test
    void findAllById() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequester_IdNot(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(request));

        List<ItemRequestResponseDto> expectedRequestsDto = List.of(MapperItemRequest.toRequestResponseDto(request));
        List<ItemRequestResponseDto> actualRequestsDto = requestService.findAllById(user.getId(), 0, 10);

        assertEquals(expectedRequestsDto, actualRequestsDto);
    }
}
