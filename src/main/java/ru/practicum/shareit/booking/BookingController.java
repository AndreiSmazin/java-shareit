package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingForRequestDto;
import ru.practicum.shareit.booking.dto.BookingForResponseDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @GetMapping("/{id}")
    public BookingForResponseDto find(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        return bookingMapper.bookingToBookingForResponseDto(bookingService.findBooking(userId, id));
    }

    @GetMapping
    public List<BookingForResponseDto> findAllForUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByUserId(userId, state).stream()
                .map(bookingMapper::bookingToBookingForResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingForResponseDto> findAllForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByOwnerId(userId, state).stream()
                .map(bookingMapper::bookingToBookingForResponseDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public BookingForResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Valid @RequestBody BookingForRequestDto bookingDto) {
        log.debug("Received POST-request /bookings with header X-Sharer-User-Id={} and body: {}", userId, bookingDto);

        return bookingMapper.bookingToBookingForResponseDto(bookingService.createNewBooking(userId, bookingDto));
    }

    @PatchMapping("/{id}")
    public BookingForResponseDto updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long id,
                                              @RequestParam(required = false) @NotNull Boolean approved) {
        log.debug("Received PATCH-request /bookings/{} with header X-Sharer-User-Id={}, and parameter: {}",
                id, userId, approved);

        return bookingMapper.bookingToBookingForResponseDto(bookingService.updateBookingStatus(userId, id, approved));
    }
}
