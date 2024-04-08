package ru.practicum.shareit.exceptions;

import java.util.Map;
import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationError(final Exception e) {
        log.error("Object validation error");

        return Map.of(
                "Error message", "Ошибка валидации.",
                "error", e.getMessage()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchElementException(final NoSuchElementException e) {
        log.error("Object not found error");

        return Map.of(
                "Error message", "Элемент не найден.",
                "error", e.getMessage()
        );
    }

    @ExceptionHandler(NotUniqueElementException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleNotUniqueElementException(final NotUniqueElementException e) {
        log.error("Object is not unique error");

        return Map.of(
                "Error message", "Не уникальный элемент.",
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
