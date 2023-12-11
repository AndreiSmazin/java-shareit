package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ErrorHandlingControllerAdvice {
    @ExceptionHandler(IdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ExceptionViolation onIdNotFoundException(IdNotFoundException e) {
        log.error("IdNotFoundException: {}", e.getMessage());

        return new ExceptionViolation(e.getMessage());
    }

    @ExceptionHandler(AccessNotAllowedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ExceptionViolation onAccessNotAllowedException(AccessNotAllowedException e) {
        log.error("AccessNotAllowedException: {}", e.getMessage());

        return new ExceptionViolation(e.getMessage());
    }

    @ExceptionHandler(RequestValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionViolation onRequestValidationException(RequestValidationException e) {
        log.error("RequestValidationException: {}", e.getMessage());

        return new ExceptionViolation(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ExceptionViolation onThrowable(Throwable e) {
        log.error("Unpredictable error: {}", e.getMessage());

        return new ExceptionViolation(e.getMessage());
    }
}
