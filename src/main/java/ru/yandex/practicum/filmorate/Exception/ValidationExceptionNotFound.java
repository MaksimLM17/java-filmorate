package ru.yandex.practicum.filmorate.Exception;

public class ValidationExceptionNotFound extends RuntimeException {
    public ValidationExceptionNotFound(String message) {
        super(message);
    }
}
