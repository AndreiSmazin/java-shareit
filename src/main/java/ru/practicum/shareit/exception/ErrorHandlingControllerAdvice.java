package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<Violation> onConstraintViolationException(ConstraintViolationException e) {
        e.getConstraintViolations().forEach(error -> log.debug("Validation error: incorrect value" +
                " '{}' of {}, {}", error.getInvalidValue(), getFieldName(error), error.getMessage()));

        return e.getConstraintViolations().stream()
                .map(error -> new Violation(getFieldName(error), error.getMessage()))
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

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String onThrowable(Throwable e) {
        log.error("Unpredictable error: {}", e.getMessage());

        return e.getMessage();
    }

    private String getFieldName(ConstraintViolation constraintViolation) {
        String[] propertyPath = constraintViolation.getPropertyPath().toString().split("\\.");
        return propertyPath[propertyPath.length - 1];
    }
}
