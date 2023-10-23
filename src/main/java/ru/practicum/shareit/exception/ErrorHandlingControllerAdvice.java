package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ErrorHandlingControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<Violation> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.getBindingResult().getFieldErrors().forEach(error -> log.error("Validation error: incorrect value" +
                " '{}' of {}, {}", error.getRejectedValue(), error.getField(), error.getDefaultMessage()));

        return e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Violation onDuplicateEmailException(DuplicateEmailException e) {
        log.error("Validation error: incorrect email, {}", e.getMessage());

        return new Violation("email", e.getMessage());
    }

    @ExceptionHandler(IdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Violation onIdNotFoundException(IdNotFoundException e) {
        log.error("IdNotFoundException: {}", e.getMessage());

        return new Violation("id", e.getMessage());
    }

    @ExceptionHandler(AccessNotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Violation onAccessNotAllowedException(AccessNotAllowedException e) {
        log.error("AccessNotAllowedException: {}", e.getMessage());

        return new Violation("userId", e.getMessage());
    }
}
