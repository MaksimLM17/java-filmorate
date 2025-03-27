package ru.yandex.practicum.filmorate.Exception;

public class ValidationExceptionBadRequest extends RuntimeException {
    public ValidationExceptionBadRequest(String message) {
        super(message);
    }
}
