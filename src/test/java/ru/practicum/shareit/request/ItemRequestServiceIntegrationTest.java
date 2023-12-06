package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest
@AutoConfigureTestDatabase
public class ItemRequestServiceIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    @DisplayName("Method findItemRequestById(long userId, long id) should return expected Request")
    void shouldReturnRequestById() throws Exception {
        final ItemResponseDto testItem = ItemResponseDto.builder()
                .id(7L)
                .name("Прожектор диодный для кино")
                .description("Мощность 200W. В наличии до 10 штук.")
                .available(true)
                .requestId(1L)
                .build();
        final ItemRequestExtendedResponseDto expectedRequest = ItemRequestExtendedResponseDto.builder()
                .id(1L)
                .description("Нужно осветительное оборудование для съемки клипа")
                .created(LocalDateTime.parse("2023-08-01T00:00:00"))
                .items(List.of(testItem))
                .build();

        final ItemRequestExtendedResponseDto request = itemRequestService.findItemRequestById(2L, 1L);

        assertEquals(expectedRequest, request, "Request and expectedRequest is not match");
    }

    @Test
    @DisplayName("Method findItemRequestById(long userId, long id) should throw IdNotFoundException when ItemRequest" +
            " is not found")
    void shouldThrowExceptionWhenRequestNotFound() throws Exception {
        final String expectedMessage = "ItemRequest with id 100 not exist";

        final IdNotFoundException e = assertThrows(
                IdNotFoundException.class,
                () -> itemRequestService.findItemRequestById(2L, 100L));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method findItemRequestsByUserId(long userId) should return correct list of ItemRequests")
    void shouldReturnItemRequestsByUserId() throws Exception {
        final List<ItemResponseDto> expectedRequest1items = List.of(ItemResponseDto.builder()
                .id(7L)
                .name("Прожектор диодный для кино")
                .description("Мощность 200W. В наличии до 10 штук.")
                .available(true)
                .requestId(1L)
                .build());
        final ItemRequestExtendedResponseDto expectedRequest1 = ItemRequestExtendedResponseDto.builder()
                .id(1L)
                .description("Нужно осветительное оборудование для съемки клипа")
                .created(LocalDateTime.parse("2023-08-01T00:00:00"))
                .items(expectedRequest1items)
                .build();
        final ItemRequestExtendedResponseDto expectedRequest2 = ItemRequestExtendedResponseDto.builder()
                .id(4L)
                .description("Нужен дрон с камерой для съемки клипа")
                .created(LocalDateTime.parse("2023-10-05T00:00:00"))
                .items(new ArrayList<>())
                .build();
        final List<ItemRequestExtendedResponseDto> expectedRequests = List.of(expectedRequest2, expectedRequest1);

        final List<ItemRequestExtendedResponseDto> requests = itemRequestService.findItemRequestsByUserId(3L);

        assertEquals(expectedRequests, requests, "Requests and expectedRequests is not match");
    }

    @Test
    @DisplayName("Method findAllItemRequests(long userId, int from, int size) should return correct list of" +
            " ItemRequests")
    void shouldReturnItemRequestsOfOtherUsers() throws Exception {
        final ItemRequestExtendedResponseDto expectedRequest1 = ItemRequestExtendedResponseDto.builder()
                .id(2L)
                .description("Возьму в аренду бетономешалку литров на 50-100")
                .created(LocalDateTime.parse("2023-08-05T00:00:00"))
                .items(new ArrayList<>())
                .build();
        final ItemRequestExtendedResponseDto expectedRequest2 = ItemRequestExtendedResponseDto.builder()
                .id(3L)
                .description("Прицеп для лодки Прогресс-10")
                .created(LocalDateTime.parse("2023-09-10T00:00:00"))
                .items(new ArrayList<>())
                .build();
        final ItemRequestExtendedResponseDto expectedRequest3 = ItemRequestExtendedResponseDto.builder()
                .id(5L)
                .description("Ищем 3 сноуборда с ботинками на зимние каникулы")
                .created(LocalDateTime.parse("2023-11-15T00:00:00"))
                .items(new ArrayList<>())
                .build();
        final List<ItemRequestExtendedResponseDto> expectedRequests = List.of(expectedRequest3,
                expectedRequest2,
                expectedRequest1);

        final List<ItemRequestExtendedResponseDto> requests = itemRequestService
                .findAllItemRequests(3L, 0, 20);

        assertEquals(expectedRequests, requests, "Requests and expectedRequests is not match");
    }
}
