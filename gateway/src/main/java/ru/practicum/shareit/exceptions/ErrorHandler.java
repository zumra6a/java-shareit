package ru.practicum.shareit.exceptions;

import java.util.Map;
import java.util.NoSuchElementException;

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
    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationError(final Exception e) {
        log.error("Object validation error");

        return Map.of(
                "Error message", "Ошибка валидации.",
                "error", e.getMessage()
        );
    }

    @ExceptionHandler({NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchElementException(final NoSuchElementException e) {
        log.error("Object not found error");

        return Map.of(
                "Error message", "Элемент не найден.",
                "error", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(final Throwable e) {
        log.error("Runtime error {}", e);

        e.printStackTrace();

        return Map.of(
                "Error message", "Ошибка выполнения.",
                "trace", e.getStackTrace().toString(),
                "error", e.getMessage()
        );
    }
}
