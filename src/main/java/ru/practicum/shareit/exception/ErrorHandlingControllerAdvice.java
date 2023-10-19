package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Getter
    @AllArgsConstructor
    public class Violation{
        private final String fieldName;
        private final String massage;
    }


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
    public Violation onDuplicateEmailException(DuplicateEmailException e){
        log.error("Validation error: incorrect email, {}", e.getMessage());

        return new Violation("email", e.getMessage());
    }

    @ExceptionHandler(IdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String onIdNotFoundException(IdNotFoundException e){
        log.error("IdNotFoundException: {}", e.getMessage());

        return e.getMessage();
    }
}
