package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentResponseDto {
    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;

    private Long itemId;
}
