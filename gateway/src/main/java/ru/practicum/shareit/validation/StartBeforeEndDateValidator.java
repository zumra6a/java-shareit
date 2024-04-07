package ru.practicum.shareit.validation;

import java.time.LocalDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

public class StartBeforeEndDateValidator implements ConstraintValidator<StartBeforeEndDate, BookItemRequestDto> {
    @Override
    public void initialize(StartBeforeEndDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(
            BookItemRequestDto bookItemRequestDto,
            ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookItemRequestDto.getStart();
        LocalDateTime end = bookItemRequestDto.getEnd();

        if (start == null || end == null) {
            return false;
        }

        return start.isBefore(end);
    }
}
