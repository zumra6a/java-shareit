package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    private static final String DATE_TIME = "2024-04-02T17:48:00";

    private BookingDto bookingDto = null;


    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse(DATE_TIME))
                .end(LocalDateTime.parse(DATE_TIME))
                .build();
    }

    @Test
    @SneakyThrows
    void itemIdSerialize() {
        assertThat(json.write(bookingDto)).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
    }

    @Test
    @SneakyThrows
    void startDateSerialize() {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    @SneakyThrows
    void endDateSerialize() {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}
