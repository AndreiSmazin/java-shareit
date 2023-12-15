package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingValidationService {
    private static final List<String> STATES = List.of(
            "ALL",
            "CURRENT",
            "PAST",
            "FUTURE",
            "WAITING",
            "REJECTED");

    public static void validateBookingPeriod(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new RequestValidationException(String.format("End booking date %s can`t be earlier than" +
                    " start date %s", end, start));
        }
    }

    public static void validateState(String state) {
        if (!STATES.contains(state)) {
            throw new RequestValidationException(String.format("Unknown state: %s", state));
        }
    }

}
