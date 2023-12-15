package ru.practicum.shareit.exception;

public class AccessNotAllowedException extends RuntimeException {
    public AccessNotAllowedException(String message) {
        super(message);
    }

    public AccessNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessNotAllowedException(Throwable cause) {
        super(cause);
    }
}
