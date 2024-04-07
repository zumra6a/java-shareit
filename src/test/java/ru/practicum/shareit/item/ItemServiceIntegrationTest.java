package ru.practicum.shareit.item;

import java.time.LocalDateTime;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private BookingService bookingService;

    private final UserDto userDto1 = UserDto.builder()
            .name("name1")
            .email("email1@email.com")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("name2")
            .email("email2@email.com")
            .build();

    private final ItemDto itemDto1 = ItemDto.builder()
            .name("item1 name")
            .description("item1 description")
            .available(true)
            .build();

    private final ItemDto itemDto2 = ItemDto.builder()
            .name("item2 name")
            .description("item2 description")
            .available(true)
            .build();

    private final ItemDto itemDtoRequest = ItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .requestId(1L)
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("request description")
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusSeconds(1L))
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .text("comment text")
            .build();

    @Test
    @SneakyThrows
    void commentItem() {
        UserDto commenter = userService.add(userDto1);
        UserDto itemOwner = userService.add(userDto2);

        ItemResponseDto addedItem = itemService.add(itemOwner.getId(), itemDto2);
        BookingResponseDto addedBooking = bookingService.add(commenter.getId(), bookingDto);

        bookingService.update(itemOwner.getId(), addedBooking.getId(), true);

        Thread.sleep(1500);

        CommentResponseDto addedComment = itemService.addComment(commenter.getId(), commentDto, addedItem.getId());

        assertEquals(1L, addedComment.getId());
        assertEquals("comment text", addedComment.getText());
    }

    @Test
    void addNewItem() {
        UserDto itemOwner = userService.add(userDto1);
        ItemResponseDto addedItem = itemService.add(itemOwner.getId(), itemDto1);

        assertEquals(1L, addedItem.getId());
        assertEquals("item1 name", addedItem.getName());
    }

    @Test
    void addItemRequest() {
        UserDto addedUser = userService.add(userDto1);
        requestService.add(addedUser.getId(), requestDto);

        ItemResponseDto addedItemRequest = itemService.add(addedUser.getId(), itemDtoRequest);

        assertEquals(1L, addedItemRequest.getRequestId());
        assertEquals("name", addedItemRequest.getName());
    }

    @Test
    void getInvalidItemById() {
        Long itemId = 3L;

        Assertions.assertThrows(RuntimeException.class,
                        () -> itemService.findOneById(userDto1.getId(), itemId));
    }
}
