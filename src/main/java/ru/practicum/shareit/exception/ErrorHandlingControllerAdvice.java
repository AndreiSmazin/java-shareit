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
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ValidationViolation> onConstraintViolationException(ConstraintViolationException e) {
        e.getConstraintViolations().forEach(error -> log.error("Validation error: incorrect value" +
                " '{}' of {}, {}", error.getInvalidValue(), getFieldName(error), error.getMessage()));

        return e.getConstraintViolations().stream()
                .map(error -> new ValidationViolation(getFieldName(error), error.getMessage()))
                .collect(Collectors.toList());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ValidationViolation> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.getBindingResult().getFieldErrors().forEach(error -> log.error("Validation error: incorrect value '{}'" +
                " of {}, {}", error.getRejectedValue(), error.getObjectName(), error.getDefaultMessage()));

        return e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationViolation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ExceptionViolation onDuplicateEmailException(DuplicateEmailException e) {
        log.error("Validation error: incorrect email, {}", e.getMessage());

        return new ExceptionViolation(e.getMessage());
    }

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

    private String getFieldName(ConstraintViolation constraintViolation) {
        String[] propertyPath = constraintViolation.getPropertyPath().toString().split("\\.");
        return propertyPath[propertyPath.length - 1];
    }
}
