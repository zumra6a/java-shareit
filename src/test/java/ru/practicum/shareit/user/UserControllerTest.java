package ru.practicum.shareit.user;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void findAll() {
        UserDto userDto = UserDto.builder()
                .name("name")
                .email("email@email.com")
                .build();
        when(userService.findAll()).thenReturn(List.of(userDto));

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> expected = List.of(userDto);

        assertEquals(objectMapper.writeValueAsString(expected), result);
    }

    @Test
    @SneakyThrows
    void findOneById() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@email.com")
                .build();
        when(userService.findOneById(userDto.getId())).thenReturn(userDto);

        String result = mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @Test
    @SneakyThrows
    void add() {
        UserDto userDto = UserDto.builder()
                .email("email@email.com")
                .name("name")
                .build();

        when(userService.add(userDto))
                .thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @Test
    @SneakyThrows
    void addWithInvalidEmail() {
        UserDto userDto = UserDto.builder()
                .email("email.com")
                .name("name")
                .build();

        when(userService.add(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDto);
    }

    @Test
    @SneakyThrows
    void addWithInvalidName() {
        UserDto userDto = UserDto.builder()
                .email("email@email.com")
                .name("     ")
                .build();

        when(userService.add(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDto);
    }

    @Test
    @SneakyThrows
    void update() {
        Long userId = 0L;
        UserDto userDto = UserDto.builder()
                .email("update@update.com")
                .name("update")
                .build();

        when(userService.update(userId, userDto)).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @Test
    @SneakyThrows
    void updateWithInvalidEmail() {
        Long userId = 0L;
        UserDto userDtoToUpdate = UserDto.builder()
                .email("update.com")
                .name("update")
                .build();

        when(userService.update(userId, userDtoToUpdate)).thenReturn(userDtoToUpdate);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userDtoToUpdate);
    }

    @Test
    @SneakyThrows
    void deleteById() {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(userId);
    }
}
