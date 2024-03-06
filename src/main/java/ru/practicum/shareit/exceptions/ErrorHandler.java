package ru.practicum.shareit.exceptions;

import java.util.Map;
import java.util.NoSuchElementException;

import javax.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationError(final ValidationException e) {
        log.error("Object validation error");

        return Map.of(
                "error", "Ошибка валидации.",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchElementException(final NoSuchElementException e) {
        log.error("Object not found error");

        return Map.of(
                "error", "Элемент не найден.",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler({NotUniqueElementException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleNotUniqueElementException(final NotUniqueElementException e) {
        log.error("Object is not unique error");

        return Map.of(
                "error", "Не уникальный элемент.",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(final RuntimeException e) {
        log.error("Runtime error");

        return Map.of(
                "error", "Ошибка выполнения.",
                "errorMessage", e.getMessage()
        );
    }
}
