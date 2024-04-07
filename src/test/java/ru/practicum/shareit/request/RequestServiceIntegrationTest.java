package ru.practicum.shareit.request;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceIntegrationTest {
    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

    private final UserDto userDto = UserDto.builder()
            .name("name")
            .email("email@email.com")
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("request description")
            .build();

    @Test
    void addRequest() {
        UserDto addedUser = userService.add(userDto);
        requestService.add(addedUser.getId(), requestDto);

        List<ItemRequestResponseDto> actual = requestService.findAllByRequesterId(addedUser.getId());

        assertEquals(1, actual.size());
        assertEquals("request description", actual.get(0).getDescription());
    }

    @Test
    void getInvalidRequest() {
        Long requestId = 5L;

        assertThrows(RuntimeException.class,
                        () -> requestService.findById(userDto.getId(), requestId));
    }
}
