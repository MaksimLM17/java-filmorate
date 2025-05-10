package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFilmRequest {
    @NotNull(message = "Не указан id при обновлении фильма")
    @Positive(message = "id фильма должен быть положительным числом")
    private Integer id;

    private String name;

    @Size(max = 200, message = "Количество символов превышает максимальное значение: 200!")
    private String description;

    @Past(message = "Дата релиза не должна быть указана в будущем времени")
    private LocalDate releaseDate;

    @Positive(message = "Укажите положительное число!")
    private Integer duration;
    private Mpa mpa;
    private List<Genre> genres;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return ! (releaseDate == null);
    }

    public boolean hasDuration() {
        return ! (duration == null);
    }

    public boolean hasGenres() {
        return ! (genres == null || genres.isEmpty());
    }

    public boolean hasMpa() {
        return ! (mpa == null);
    }

}

