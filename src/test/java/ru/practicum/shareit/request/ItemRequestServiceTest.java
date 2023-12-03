package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    private ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();
    private ItemMapper itemMapper = new ItemMapperImpl();
    private ItemRequestService itemRequestService;

    @BeforeEach
    void initiateItemRequestService() {
        itemRequestService = new ItemRequestServiceDbImpl(itemRequestRepository,
                itemRepository,
                userService,
                itemRequestMapper,
                itemMapper);
    }

    @Test
    @DisplayName("Method checkItemRequest(long id) should return correct ItemRequest")
    void shouldReturnItemRequest() throws Exception {
        final User testUser = User.builder()
                .id(3L)
                .name("Степан Калюжный")
                .email("KaluzhnyS@gmail.com")
                .build();
        final ItemRequest expectedRequest = ItemRequest.builder()
                .id(1L)
                .description("Нужно осветительное оборудование для съемки клипа")
                .requester(testUser)
                .created(LocalDateTime.parse("2023-08-01T00:00:00"))
                .build();
        Mockito.when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(expectedRequest));

        final ItemRequest request = itemRequestService.checkItemRequest(1L);

        assertEquals(expectedRequest, request, "Request and expectedRequest is not match");
    }

    @Test
    @DisplayName("Method checkItemRequest(long id) should throw IdNotFoundException when ItemRequest is not found")
    void shouldThrowExceptionWhenItemRequestNotFound() throws Exception {
        final String expectedMessage = "ItemRequest with id 100 not exist";

        Mockito.when(itemRequestRepository.findById(100L)).thenReturn(Optional.empty());

        final Exception e = assertThrows(IdNotFoundException.class, () ->
                itemRequestService.checkItemRequest(100L));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method createNewItemRequest(long userId, ItemRequestDto itemRequestDto) should return correct" +
            " created ItemRequest")
    void shouldCreateItemRequest() throws Exception {
        ItemRequestForResponseDto expectedRequest = ItemRequestForResponseDto.builder()
                .id(1L)
                .description("Нужно осветительное оборудование для съемки клипа")
                .created(LocalDateTime.parse("2023-08-01T00:00:00"))
                .build();
        final ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Нужно осветительное оборудование для съемки клипа")
                .build();

        final User testUser = User.builder()
                .id(3L)
                .name("Степан Калюжный")
                .email("KaluzhnyS@gmail.com")
                .build();
        Mockito.when(userService.checkUser(3L)).thenReturn(testUser);

        ItemRequest returnedRequest = ItemRequest.builder()
                .id(1L)
                .description("Нужно осветительное оборудование для съемки клипа")
                .requester(testUser)
                .created(LocalDateTime.parse("2023-08-01T00:00:00"))
                .build();
        Mockito.when(itemRequestRepository.save(ArgumentMatchers.any(ItemRequest.class))).thenReturn(returnedRequest);

        final ItemRequestForResponseDto createdRequest = itemRequestService.createNewItemRequest(3L, requestDto);

        assertEquals(expectedRequest, createdRequest, "createdRequest and expectedRequest is not match");
    }

    @Test
    @DisplayName("Methods findItemRequestById, findItemRequestsByUserId, findAllItemRequests and createNewItemRequest" +
            " should throw IdNotFoundException when User is not found")
    void shouldThrowExceptionWhenUserNotExists() throws Exception {
        final String expectedMessage = "User with id 100 not exist";

        Mockito.when(userService.checkUser(100L)).thenThrow(new IdNotFoundException("User with id 100 not exist"));

        final Exception findItemRequestByIdException = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemRequestService.findItemRequestById(100L, 1L));

        Assertions.assertEquals(expectedMessage, findItemRequestByIdException.getMessage(), "Exception massage and" +
                " expectedMassage is not match");

        final Exception findItemRequestsByUserIdException = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemRequestService.findItemRequestsByUserId(100L));

        Assertions.assertEquals(expectedMessage, findItemRequestsByUserIdException.getMessage(), "Exception massage and" +
                " expectedMassage is not match");

        final Exception findAllItemRequestsException = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemRequestService.findAllItemRequests(100L, 0, 20));

        Assertions.assertEquals(expectedMessage, findAllItemRequestsException.getMessage(), "Exception massage and" +
                " expectedMassage is not match");

        final ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Нужно осветительное оборудование для съемки клипа")
                .build();
        final Exception createNewItemRequestException = Assertions.assertThrows(IdNotFoundException.class, () ->
                itemRequestService.createNewItemRequest(100L, requestDto));

        Assertions.assertEquals(expectedMessage, createNewItemRequestException.getMessage(), "Exception massage and" +
                " expectedMassage is not match");
    }
}
