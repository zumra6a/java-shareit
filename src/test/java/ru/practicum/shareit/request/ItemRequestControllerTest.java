package ru.practicum.shareit.request;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.headers.WithUserHeaderID;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest implements WithUserHeaderID {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService requestService;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final ItemRequestResponseDto requestResponseDto = ItemRequestResponseDto.builder()
            .id(1L)
            .description("description")
            .created(LocalDateTime.now())
            .items(List.of())
            .build();

    @Test
    @SneakyThrows
    void add() {
        when(requestService.add(any(), any()))
                .thenReturn(requestResponseDto);

        String result = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header(HEADER_USER_ID, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(requestResponseDto), result);
    }

    @Test
    @SneakyThrows
    void findAllByRequesterId() {
        when(requestService.findAllByRequesterId(user.getId()))
                .thenReturn(List.of(requestResponseDto));

        String result = mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header(HEADER_USER_ID, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(requestResponseDto)), result);
    }

    @Test
    @SneakyThrows
    void findById() {
        Long requestId = 1L;
        when(requestService.findById(user.getId(), requestId))
                .thenReturn(requestResponseDto);

        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header(HEADER_USER_ID, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(requestResponseDto), result);
    }

    @Test
    @SneakyThrows
    void findAllById() {
        Integer from = 0;
        Integer size = 10;
        when(requestService.findAllById(user.getId(), from, size))
                .thenReturn(List.of(requestResponseDto));

        String result = mockMvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header(HEADER_USER_ID, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(requestResponseDto)), result);
    }
}
