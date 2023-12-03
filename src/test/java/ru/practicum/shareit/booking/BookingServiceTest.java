package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingForRequestDto;
import ru.practicum.shareit.booking.dto.BookingForResponseDto;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserForBookingDto;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    private BookingMapper bookingMapper = new BookingMapperImpl();
    private BookingService bookingService;

    @BeforeEach
    void initiateBookingService() {
        bookingService = new BookingServiceDbImpl(bookingRepository,
                userService,
                itemService,
                bookingMapper);
    }

    @Test
    @DisplayName("Method checkBooking(long id) should return correct Booking")
    void shouldReturnBooking() throws Exception {
        final Booking expectedBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(expectedBooking));

        final Booking booking = bookingService.checkBooking(1L);

        assertEquals(expectedBooking, booking, "Booking and expectedBooking is not match");
    }

    @Test
    @DisplayName("Method checkBooking(long id) should throw IdNotFoundException when Booking is not found")
    void shouldThrowExceptionWhenBookingNotFound() throws Exception {
        final String expectedMessage = "Booking with id 100 not exist";

        Mockito.when(bookingRepository.findById(100L)).thenReturn(Optional.empty());

        final Exception e = assertThrows(IdNotFoundException.class, () ->
                bookingService.checkBooking(100L));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method createNewBooking(long userId, BookingForRequestDto bookingDto) should return correct" +
            " created Booking")
    void shouldCreateNewBooking() throws Exception {
        final UserForBookingDto expectedBookingBooker = UserForBookingDto.builder()
                .id(4L)
                .build();
        final ItemForBookingDto expectedBookingItem = ItemForBookingDto.builder()
                .id(3L)
                .name("Байдарка трёхместная Ладога")
                .build();
        final BookingForResponseDto expectedBooking = BookingForResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .status(BookingStatus.WAITING)
                .booker(expectedBookingBooker)
                .item(expectedBookingItem)
                .build();
        final BookingForRequestDto bookingDto = BookingForRequestDto.builder()
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .itemId(3L)
                .build();

        final User user = User.builder()
                .id(4L)
                .name("Иван Иванов")
                .email("Ivan1992@mail.ru")
                .build();
        Mockito.when(userService.checkUser(4L)).thenReturn(user);

        final User itemOwner = User.builder()
                .id(5L)
                .name("Максим Акропович")
                .email("SuperMax2003@mail.ru")
                .build();
        final Item item = Item.builder()
                .id(3L)
                .name("Байдарка трёхместная Ладога")
                .description("Каркасно-надувная, в чехле, весла и насос в комплекте")
                .owner(itemOwner)
                .available(true)
                .requestId(null)
                .build();
        Mockito.when(itemService.checkItem(3L)).thenReturn(item);

        final Booking booking = Booking.builder()
                .id(0)
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        final Booking newBooking = Booking.builder()
                .id(1)
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        Mockito.when(bookingRepository.save(booking)).thenReturn(newBooking);

        final BookingForResponseDto savedBooking = bookingService.createNewBooking(4L, bookingDto);

        assertEquals(expectedBooking, savedBooking, "SavedBooking and expectedBooking is not match");
    }

    @Test
    @DisplayName("Method createNewBooking(long userId, BookingForRequestDto bookingDto) should throw" +
            " AccessNotAllowedException when User is owner of Item")
    void shouldThrowExceptionWhenUserIsOwnerOfItem() throws Exception {
        final String expectedMessage = "User 4 is owner of item 3";
        final BookingForRequestDto bookingDto = BookingForRequestDto.builder()
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .itemId(3L)
                .build();

        final User user = User.builder()
                .id(4L)
                .name("Иван Иванов")
                .email("Ivan1992@mail.ru")
                .build();
        Mockito.when(userService.checkUser(4L)).thenReturn(user);

        final Item item = Item.builder()
                .id(3L)
                .name("Байдарка трёхместная Ладога")
                .description("Каркасно-надувная, в чехле, весла и насос в комплекте")
                .owner(user)
                .available(true)
                .requestId(null)
                .build();
        Mockito.when(itemService.checkItem(3L)).thenReturn(item);

        final Exception e = assertThrows(AccessNotAllowedException.class, () ->
                bookingService.createNewBooking(4L, bookingDto));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method createNewBooking(long userId, BookingForRequestDto bookingDto) should throw" +
            " RequestValidationException when Item is not available")
    void shouldThrowExceptionWhenItemNotAvailable() throws Exception {
        final String expectedMessage = "Item 3 not available";
        final BookingForRequestDto bookingDto = BookingForRequestDto.builder()
                .start(LocalDateTime.parse("2023-08-01T00:00:00"))
                .end(LocalDateTime.parse("2023-08-10T00:00:00"))
                .itemId(3L)
                .build();

        final User user = User.builder()
                .id(4L)
                .name("Иван Иванов")
                .email("Ivan1992@mail.ru")
                .build();
        Mockito.when(userService.checkUser(4L)).thenReturn(user);

        final User itemOwner = User.builder()
                .id(5L)
                .name("Максим Акропович")
                .email("SuperMax2003@mail.ru")
                .build();
        final Item item = Item.builder()
                .id(3L)
                .name("Байдарка трёхместная Ладога")
                .description("Каркасно-надувная, в чехле, весла и насос в комплекте")
                .owner(itemOwner)
                .available(false)
                .requestId(null)
                .build();
        Mockito.when(itemService.checkItem(3L)).thenReturn(item);

        final Exception e = assertThrows(RequestValidationException.class, () ->
                bookingService.createNewBooking(4L, bookingDto));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }

    @Test
    @DisplayName("Method createNewBooking(long userId, BookingForRequestDto bookingDto) should throw" +
            " RequestValidationException when start date later then end date")
    void shouldThrowExceptionWhenStartLaterThanEnd() throws Exception {
        final String expectedMessage = "End booking date 2023-08-01T00:00 can`t be earlier than start date" +
                " 2023-08-10T00:00";
        final BookingForRequestDto bookingDto = BookingForRequestDto.builder()
                .start(LocalDateTime.parse("2023-08-10T00:00:00"))
                .end(LocalDateTime.parse("2023-08-01T00:00:00"))
                .itemId(3L)
                .build();

        final User user = User.builder()
                .id(4L)
                .name("Иван Иванов")
                .email("Ivan1992@mail.ru")
                .build();
        Mockito.when(userService.checkUser(4L)).thenReturn(user);

        final User itemOwner = User.builder()
                .id(5L)
                .name("Максим Акропович")
                .email("SuperMax2003@mail.ru")
                .build();
        final Item item = Item.builder()
                .id(3L)
                .name("Байдарка трёхместная Ладога")
                .description("Каркасно-надувная, в чехле, весла и насос в комплекте")
                .owner(itemOwner)
                .available(true)
                .requestId(null)
                .build();
        Mockito.when(itemService.checkItem(3L)).thenReturn(item);

        final Exception e = assertThrows(RequestValidationException.class, () ->
                bookingService.createNewBooking(4L, bookingDto));

        assertEquals(expectedMessage, e.getMessage(), "Exception massage and expectedMassage is not match");
    }
}
