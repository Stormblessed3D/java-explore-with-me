package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;

import static ru.practicum.constantkeeper.ConstantKeeper.DATE_TIME_FORMATTER;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class, NumberFormatException.class,
            MissingServletRequestParameterException.class, EventTimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(Exception e) {
        log.warn("Получен статус 400 Bad request {}", e.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST.toString(),"Incorrectly made request",
                e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(ConstraintViolationException e) {
        log.warn("Получен статус 409 Conflict {}", e.getMessage());
        return new ApiError(HttpStatus.CONFLICT.toString(),"Integrity constraint has been violated.",
                e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(DataIntegrityViolationException e) {
        log.warn("Получен статус 409 Conflict {}", e.getMessage());
        return new ApiError(HttpStatus.CONFLICT.toString(),"Integrity constraint has been violated.",
                e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler({EventStatusException.class, RequestConstraintException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(RuntimeException e) {
        log.warn("Получен статус 409 Conflict {}", e.getMessage());
        return new ApiError(HttpStatus.CONFLICT.toString(),"For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(CategoryViolationException e) {
        log.warn("Получен статус 409 Conflict {}", e.getMessage());
        return new ApiError(HttpStatus.CONFLICT.toString(),"For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(CommentException e) {
        log.warn("Получен статус 409 Conflict {}", e.getMessage());
        return new ApiError(HttpStatus.CONFLICT.toString(),"For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(EntityNotFoundException e) {
        log.warn("Получен статус 404 Not found {}", e.getMessage(), e);
        return new ApiError(HttpStatus.NOT_FOUND.toString(),"The required object was not found.",
                e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle(Throwable e) {
        log.warn("Получен статус 500 Internal ServerError {}", e.getMessage(), e);
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.toString(),"Internal Server Error",
                e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }
}
