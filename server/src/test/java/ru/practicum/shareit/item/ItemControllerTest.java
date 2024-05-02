package ru.practicum.shareit.item;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.headers.WithUserHeaderID;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest implements WithUserHeaderID {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("email@adress.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    @Test
    @SneakyThrows
    void createItem() {
        Long userId = 0L;
        ItemDto itemDto = MapperItem.toItemDto(item);

        when(itemService.add(userId, itemDto))
                .thenReturn(MapperItem.toItemResponseDto(item));

        String result = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header(HEADER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDto actual = objectMapper.readValue(result, ItemDto.class);

        assertEquals(itemDto.getDescription(), actual.getDescription());
        assertEquals(itemDto.getName(), actual.getName());
        assertEquals(itemDto.getAvailable(), actual.getAvailable());
    }

    @Test
    @SneakyThrows
    void updateExistingItem() {
        Long itemId = 0L;
        Long userId = 0L;
        ItemDto itemDto = MapperItem.toItemDto(item);

        when(itemService.update(userId, itemId, itemDto))
                .thenReturn(MapperItem.toItemResponseDto(item));

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header(HEADER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDto actual = objectMapper.readValue(result, ItemDto.class);

        assertEquals(itemDto.getDescription(), actual.getDescription());
        assertEquals(itemDto.getName(), actual.getName());
        assertEquals(itemDto.getAvailable(), actual.getAvailable());
    }

    @Test
    @SneakyThrows
    void getItemById() {
        Long itemId = 0L;
        Long userId = 0L;
        ItemResponseDto itemResponseDto = MapperItem.toItemResponseDto(item);

        when(itemService.findOneById(userId, itemId))
                .thenReturn(itemResponseDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header(HEADER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(itemResponseDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemResponseDto), result);
    }

    @Test
    @SneakyThrows
    void getAllItems() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        List<ItemResponseDto> itemResponseDtoList = List.of(MapperItem.toItemResponseDto(item));

        when(itemService.findAllByUserId(userId, from, size))
                .thenReturn(itemResponseDtoList);

        String result = mockMvc.perform(get("/items", from, size)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemResponseDtoList), result);
    }

    @Test
    @SneakyThrows
    void searchItemsByNameAndDescription() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        String text = "find";
        List<ItemResponseDto> itemResponseDtoList = List.of(MapperItem.toItemResponseDto(item));

        when(itemService.search(userId, text, from, size))
                .thenReturn(itemResponseDtoList);

        String result = mockMvc.perform(get("/items/search", from, size)
                        .header(HEADER_USER_ID, userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemResponseDtoList), result);
    }

    @Test
    @SneakyThrows
    void createComment() {
        CommentDto commentToAdd = CommentDto.builder()
                .text("some comment")
                .build();
        CommentResponseDto commentDtoOut = CommentResponseDto.builder()
                .id(1L)
                .itemId(item.getId())
                .text(commentToAdd.getText())
                .build();

        when(itemService.addComment(user.getId(), commentToAdd, item.getId()))
                .thenReturn(commentDtoOut);

        String result = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .contentType("application/json")
                        .header(HEADER_USER_ID, user.getId())
                        .content(objectMapper.writeValueAsString(commentToAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDtoOut), result);
    }
}
