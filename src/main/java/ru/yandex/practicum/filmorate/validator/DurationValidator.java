package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.DurationPositive;

import java.time.Duration;


public class DurationValidator implements ConstraintValidator<DurationPositive, Duration> {

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        if (duration == null) {
            return true;
        }
        return !duration.isNegative();
    }
}
