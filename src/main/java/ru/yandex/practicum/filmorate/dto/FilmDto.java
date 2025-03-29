package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.DurationPositive;
import ru.yandex.practicum.filmorate.serializer.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmDto {
    private Integer id;

    @NotNull(message = "Название фильма не указано!")
    @NotEmpty(message = "Передано пустое название фильма!")
    private String name;

    @Size(max = 200, message = "Количество символов превышает максимальное значение: 200!")
    private String description;
    @Past(message = "Дата релиза не должна быть указана в будущем времени")
    private LocalDate releaseDate;

    @DurationPositive(message = "Укажите положительное число!")
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
}
