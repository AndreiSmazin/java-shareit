package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessNotAllowedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceDbImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto findBooking(long userId, long id) {
        userService.checkUser(userId);

        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Booking with id %s not exist", id)));
        validateViewer(userId, booking);

        return bookingMapper.bookingToBookingForResponseDto(booking);
    }

    @Override
    public Booking checkBooking(long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new IdNotFoundException(String.format("Booking with id %s not exist", id)));
    }

    @Override
    public List<BookingResponseDto> findAllBookingsByUserId(long userId, String state, int from, int size) {
        userService.checkUser(userId);

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdOrderByStartDesc(userId);

        return filterBookingsByState(bookings, state).stream()
                .map(bookingMapper::bookingToBookingForResponseDto)
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> findAllBookingsByOwnerId(long userId, String state, int from, int size) {
        userService.checkUser(userId);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdOrderByStartDesc(userId);

        return filterBookingsByState(bookings, state).stream()
                .map(bookingMapper::bookingToBookingForResponseDto)
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDto createNewBooking(long userId, BookingCreateDto bookingDto) {
        log.debug("+ createNewBooking: {}, {}", userId, bookingDto);

        Booking booking = new Booking();
        User booker = userService.checkUser(userId);
        Item item = itemService.checkItem(bookingDto.getItemId());

        validateBooker(booker, item);
        booking.setBooker(booker);

        validateAvailable(item);
        booking.setItem(item);

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());

        booking.setStatus(BookingStatus.WAITING);

        return bookingMapper.bookingToBookingForResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto updateBookingStatus(long userId, long id, boolean approved) {
        log.debug("+ updateBookingStatus: {}, {}, {}", userId, id, approved);

        userService.checkUser(userId);
        Booking booking = checkBooking(id);
        validateOwner(userId, booking);

        validateApproved(booking);
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        bookingRepository.save(booking);

        return bookingMapper.bookingToBookingForResponseDto(booking);
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
                return bookings;
        }
    }
}
