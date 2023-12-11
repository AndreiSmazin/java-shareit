package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> find(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        return bookingClient.getBooking(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllBookingsByOwnerId(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @Valid @RequestBody BookingCreateDto bookingDto) {
        log.debug("Received POST-request /bookings with header X-Sharer-User-Id={} and body: {}", userId, bookingDto);

        return bookingClient.createNewBooking(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long id,
                                           @RequestParam(required = false) @NotNull Boolean approved) {
        log.debug("Received PATCH-request /bookings/{} with header X-Sharer-User-Id={}, and parameter: {}",
                id, userId, approved);

        return bookingClient.updateBookingStatus(userId, id, approved);
    }
}
