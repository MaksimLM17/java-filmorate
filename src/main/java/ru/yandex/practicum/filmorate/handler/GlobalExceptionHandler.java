package ru.yandex.practicum.filmorate.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationExceptionNotFound(NotFoundException e) {
        return ErrorResponse.builder().message("Ошибка валидации").details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptionBadRequest(BadRequestException e) {
        return ErrorResponse.builder().message("Ошибка валидации").details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationMethodExceptions(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorMessage.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; \n");
        });
        log.error("Ошибка валидации: {}", errorMessage);
        return ErrorResponse.builder().message("Ошибка валидации").details(errorMessage.toString()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintException(ConstraintViolationException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getConstraintViolations().forEach(violation -> {
            errorMessage.append(violation.getPropertyPath())
                    .append(": ")
                    .append(violation.getMessage())
                    .append("; \n");
        });
        log.error("Ошибка валидации: {}", errorMessage);
        return ErrorResponse.builder().message("Ошибка валидации").details(errorMessage.toString()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String parameterName = e.getName();
        String parameterValue = String.valueOf(e.getValue());
        String errorMessage = String.format("Передано некорректное значение параметра %s: %s",
                parameterName,parameterValue);
        log.error(errorMessage);
        return ErrorResponse.builder().message(errorMessage).details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnknown(Exception e, HttpServletRequest request) {
        log.error("Произошло неизвестное исключение при запросе с методом: {}," +
                "адрес запроса: {},   с ошибкой: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return ErrorResponse.builder().message("Произошло неизвестное исключение, проверьте данные запроса")
                .details(e.getMessage()).build();
    }
}
