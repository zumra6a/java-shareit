package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.MapperComment;
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapperUser;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final UserDto userDto = MapperUser.toUserDto(user);

    private final Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final ItemResponseDto itemResponseDto = MapperItem.toItemResponseDto(item).toBuilder()
            .comments(Collections.emptyList())
            .build();

    private final Comment comment = Comment.builder()
            .id(1L)
            .text("comment")
            .created(LocalDateTime.now())
            .author(user)
            .item(item)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .build();

    @Test
    void add() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(item))
                .thenReturn(item);

        ItemResponseDto actual = itemService.add(userDto.getId(), MapperItem.toItemDto(item));

        assertEquals(actual.getName(), "name");
        assertEquals(actual.getDescription(), "description");
    }

    @Test
    void update() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        Item updatedItem = item.toBuilder()
                .name("updated name")
                .description("updated description")
                .available(false)
                .owner(user)
                .request(itemRequest)
                .build();

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(updatedItem));

        ItemResponseDto savedItem = itemService.update(
                user.getId(),
                item.getId(),
                MapperItem.toItemDto(updatedItem));

        assertEquals("updated name", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
    }

    @Test
    void findOneById() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        ItemResponseDto actual = itemService.findOneById(user.getId(), item.getId());

        assertEquals(itemResponseDto, actual);
    }

    @Test
    void findAllByUserId() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(item));
        when(commentRepository.findAllByItemIdIn(List.of(item.getId())))
                .thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemInAndStatus(any(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<ItemResponseDto> actual = itemService.findAllByUserId(user.getId(), 0, 10);

        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getId());
        assertEquals("name", actual.get(0).getName());
    }

    @Test
    void search() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.searchAvailableItemsByNameAndDescription(anyString(), any(Pageable.class)))
                .thenReturn(List.of(item));

        List<ItemResponseDto> actual = itemService.search(user.getId(), "text", 0, 10);

        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getId());
        assertEquals("name", actual.get(0).getName());
    }

    @Test
    void addComment() {
        User commenter = user.toBuilder()
                .id(2L)
                .build();
        Comment userComment = comment.toBuilder()
                .id(null)
                .author(commenter)
                .build();

        when(userRepository.findById(commenter.getId()))
                .thenReturn(Optional.of(commenter));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(userComment);

        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();

        CommentResponseDto expected = MapperComment.toCommentResponseDto(userComment);
        CommentResponseDto actual = itemService.addComment(commenter.getId(), commentDto, item.getId());

        assertEquals(expected, actual);
    }
}
