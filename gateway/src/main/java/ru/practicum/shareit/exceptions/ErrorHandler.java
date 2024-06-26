package ru.practicum.shareit.exceptions;

import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ValidationException.class,
            ConstraintViolationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationError(final Exception e) {
        log.error("Object validation error");

        return Map.of(
                "Error message", "Ошибка валидации.",
                "error", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(final Throwable e) {
        log.error("Runtime error {}", e);

        return Map.of(
                "Error message", "Ошибка выполнения.",
                "error", e.getMessage()
        );
    }
}
