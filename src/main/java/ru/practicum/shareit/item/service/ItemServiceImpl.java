package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.MapperBooking;
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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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
                    .findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatus(
                            item.getId(),
                            userId,
                            LocalDateTime.now(),
                            BookingStatus.APPROVED,
                            Sort.by(Sort.Direction.DESC, "start"));

            if (last != null) {
                itemResponseDtoBuilder.lastBooking(ItemBookingResponseDto.builder()
                                .id(last.getId())
                                .bookerId(last.getBooker().getId())
                        .build());
            }

            Booking next = bookingRepository
                    .findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatus(
                            item.getId(),
                            userId,
                            LocalDateTime.now(),
                            BookingStatus.APPROVED,
                            Sort.by(Sort.Direction.ASC, "start"));

            if (next != null) {
                itemResponseDtoBuilder.nextBooking(ItemBookingResponseDto.builder()
                        .id(next.getId())
                        .bookerId(next.getBooker().getId())
                        .build());
            }
        }

        List<CommentResponseDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(MapperComment::toCommentResponseDto)
                .collect(toList());

        itemResponseDtoBuilder.comments(comments);

        return itemResponseDtoBuilder.build();
    }

    @Override
    public List<ItemResponseDto> findAllByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("User %d not found", userId)));

        List<Item> userItems = itemRepository.findAllByOwnerId(userId);

        List<Long> userItemIdList = userItems.stream()
                .map(Item::getId)
                .collect(toList());

        Map<Long, List<CommentResponseDto>> comments = commentRepository.findAllByItemIdIn(userItemIdList).stream()
                .map(MapperComment::toCommentResponseDto)
                .collect(groupingBy(CommentResponseDto::getItemId, toList()));

        Map<Long, List<BookingResponseDto>> bookings = bookingRepository.findAllByItemInAndStatus(
                        userItems,
                        BookingStatus.APPROVED,
                        Sort.by(Sort.Direction.ASC, "start")).stream()
                .map(MapperBooking::toBookingResponseDto)
                .collect(groupingBy(BookingResponseDto::getItemId, toList()));

        return userItems
                .stream()
                .map(item -> {
                    ItemResponseDto.ItemResponseDtoBuilder itemResponseDtoBuilder = MapperItem.toItemResponseDto(item)
                            .toBuilder();

                    itemResponseDtoBuilder.comments(comments.get(item.getId()));

                    List<BookingResponseDto> itemBookings = bookings.get(item.getId());
                    LocalDateTime filterDate = LocalDateTime.now();
                    BookingResponseDto last = filterLastBooking(itemBookings, filterDate);
                    BookingResponseDto next = filterNextBooking(itemBookings, filterDate);

                    if (last != null) {
                        itemResponseDtoBuilder.lastBooking(ItemBookingResponseDto.builder()
                                .id(last.getId())
                                .bookerId(last.getBooker().getId())
                                .build());
                    }

                    if (next != null) {
                        itemResponseDtoBuilder.nextBooking(ItemBookingResponseDto.builder()
                                .id(next.getId())
                                .bookerId(next.getBooker().getId())
                                .build());
                    }

                    return itemResponseDtoBuilder.build();
                })
                .collect(toList());
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

    private BookingResponseDto filterLastBooking(List<BookingResponseDto> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(booking -> {
                    LocalDateTime start = booking.getStart();

                    return  start.isBefore(time) || start.equals(time);
                })
                .findFirst()
                .orElse(null);
    }

    private BookingResponseDto filterNextBooking(List<BookingResponseDto> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(booking -> booking.getStart().isAfter(time))
                .findFirst()
                .orElse(null);
    }

}
