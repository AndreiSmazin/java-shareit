package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.request.dto.ExtendedItemRequestForResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class ItemRequestServiceIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    @DisplayName("Method findItemRequestById(long userId, long id) should return expected Request")
    void shouldReturnRequestById() throws Exception {
        final ItemForResponseDto testItem = ItemForResponseDto.builder()
                .id(7L)
                .name("Прожектор диодный для кино")
                .description("Мощность 200W. В наличии до 10 штук.")
                .available(true)
                .requestId(1L)
                .build();
        final ExtendedItemRequestForResponseDto expectedRequest = ExtendedItemRequestForResponseDto.builder()
                .id(1L)
                .description("Нужно осветительное оборудование для съемки клипа")
                .created(LocalDateTime.parse("2023-08-01T00:00:00"))
                .items(List.of(testItem))
                .build();

        final ExtendedItemRequestForResponseDto request = itemRequestService.findItemRequestById(2L, 1L);

        assertEquals(expectedRequest, request, "Request and expectedRequest is not match");
    }

    @Test
    @DisplayName("Method findItemRequestById(long userId, long id) should throw IdNotFoundException when Booking is not found")
    void shouldThrowExceptionWhenRequestNotFound() throws Exception {
        final String expectedMessage = "ItemRequest with id 100 not exist";

        final IdNotFoundException e = assertThrows(
                IdNotFoundException.class,
                () -> itemRequestService.findItemRequestById(2L, 100L));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }
}
