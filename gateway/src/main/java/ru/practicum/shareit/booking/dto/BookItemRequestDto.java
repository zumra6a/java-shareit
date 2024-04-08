package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.StartBeforeEndDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDate
public class BookItemRequestDto {
	@NotNull
	private long itemId;

	@FutureOrPresent
	private LocalDateTime start;

	private LocalDateTime end;
}
