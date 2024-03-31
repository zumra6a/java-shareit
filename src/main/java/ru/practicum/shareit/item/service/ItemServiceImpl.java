package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemBookingResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.MapperComment;
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(
            ItemRepository itemRepository,
            UserRepository userRepository,
            CommentRepository commentRepository,
            BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ItemResponseDto add(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));

        Item item = MapperItem.toItem(itemDto);
        item.setOwner(user);
        item = itemRepository.save(item);

        return MapperItem.toItemResponseDto(item);
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long userId, Long itemId, ItemDto itemDto) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Item with id %d not found", itemId)));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NoSuchElementException(String.format("User %d has not item with id %d", userId, itemId));
        }

        Boolean isAvailable = itemDto.getAvailable();
        if (!Objects.isNull(isAvailable)) {
            item.setAvailable(isAvailable);
        }

        String description = itemDto.getDescription();
        if (!Objects.isNull(description) && !description.isBlank()) {
            item.setDescription(description);
        }

        String name = itemDto.getName();
        if (!Objects.isNull(name) && !name.isBlank()) {
            item.setName(name);
        }

        itemRepository.save(item);

        return MapperItem.toItemResponseDto(item);
    }

    @Override
    public ItemResponseDto findOneById(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Item with id %d not found", itemId)));

        ItemResponseDto.ItemResponseDtoBuilder itemResponseDtoBuilder = MapperItem.toItemResponseDto(item).toBuilder();

        if (item.isOwnedBy(userId)) {
            Booking last = bookingRepository
                    .findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
                            item.getId(),
                            userId,
                            LocalDateTime.now(),
                            BookingStatus.APPROVED);

            if (last != null) {
                itemResponseDtoBuilder.lastBooking(ItemBookingResponseDto.builder()
                                .id(last.getId())
                                .bookerId(last.getBooker().getId())
                        .build());
            }

            Booking next = bookingRepository
                    .findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
                            item.getId(),
                            userId,
                            LocalDateTime.now(),
                            BookingStatus.APPROVED);

            if (next != null) {
                itemResponseDtoBuilder.nextBooking(ItemBookingResponseDto.builder()
                        .id(next.getId())
                        .bookerId(next.getBooker().getId())
                        .build());
            }
        }

        commentRepository.findByItemId(itemId).stream().forEach(comment -> {
            itemResponseDtoBuilder.comment(MapperComment.toCommentResponseDto(comment));
        });

        return itemResponseDtoBuilder.build();
    }

    @Override
    public List<ItemResponseDto> findAllByUserId(Long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map((item -> findOneById(userId, item.getId())))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ItemResponseDto> search(Long userId, String text) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchAvailableItemsByNameAndDescription(text).stream()
                .map(MapperItem::toItemResponseDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Item with id %d not found", itemId)));

        bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new IllegalStateException(String.format("User %d not booked %d", userId, itemId)));

        if (item.isOwnedBy(userId)) {
            throw new IllegalStateException(String.format("User %d owns item %d", userId, itemId));
        }

        Comment comment = Comment.builder()
                .item(item)
                .author(user)
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return MapperComment.toCommentResponseDto(comment);
    }

}
