package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForRequestDto;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceDbImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingServiceDbImpl(BookingRepository bookingRepository,
                                @Qualifier("userServiceDbImpl") UserService userService,
                                @Qualifier("itemServiceDbImpl") ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public Booking findBooking(long userId, long id) {
        userService.findUser(userId);

        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Booking with id %s not exist", id)));
        validateViewer(userId, booking);

        return booking;
    }

    @Override
    public Booking findBooking(long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Booking with id %s not exist", id)));
    }

    @Override
    public List<Booking> findAllBookingsByUserId(long userId, String state) {
        userService.findUser(userId);

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);

        return filterBookingsByState(bookings, state);
    }

    @Override
    public List<Booking> findAllBookingsByOwnerId(long userId, String state) {
        userService.findUser(userId);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);

        return filterBookingsByState(bookings, state);
    }

    @Override
    public Booking createNewBooking(long userId, BookingForRequestDto bookingDto) {
        log.debug("+ createNewBooking: {}, {}", userId, bookingDto);

        Booking booking = new Booking();
        User booker = userService.findUser(userId);
        Item item = itemService.findItem(bookingDto.getItemId());

        validateBooker(booker, item);
        booking.setBooker(booker);

        validateAvailable(item);
        booking.setItem(item);

        validateBookingPeriod(bookingDto.getStart(), bookingDto.getEnd());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());

        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(long userId, long id, boolean approved) {
        log.debug("+ updateBookingStatus: {}, {}, {}", userId, id, approved);

        userService.findUser(userId);
        Booking booking = findBooking(id);
        validateOwner(userId, booking);

        validateApproved(booking);
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    private void validateBookingPeriod(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new RequestValidationException(String.format("End booking date %s can`t be earlier than" +
                    " start date %s", end, start));
        }
    }

    private void validateAvailable(Item item) {
        if (!item.isAvailable()) {
            throw new RequestValidationException(String.format("Item %s not available", item.getId()));
        }
    }

    private void validateOwner(Long userId, Booking booking) {
        if (userId != booking.getItem().getOwner().getId()) {
            throw new AccessNotAllowedException(String.format("User %s does not have access to target booking",
                    userId));
        }
    }

    private void validateViewer(Long userId, Booking booking) {
        if (!(userId == booking.getItem().getOwner().getId() || userId == booking.getBooker().getId())) {
            throw new AccessNotAllowedException(String.format("User %s does not have access to target booking",
                    userId));
        }
    }

    private void validateApproved(Booking booking) {
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new RequestValidationException(String.format("Booking %s is already approved", booking.getId()));
        }
    }

    private void validateBooker(User booker, Item item) {
        if (booker.equals(item.getOwner())) {
            throw new AccessNotAllowedException(String.format("User %s is owner of item %s", booker.getId(),
                    item.getId()));
        }
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {
        switch (state) {
            case "ALL":
                return bookings;
            case "CURRENT":
                return bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                                booking.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList());
            default:
                throw new RequestValidationException(String.format("Unknown state: %s", state));
        }
    }
}
