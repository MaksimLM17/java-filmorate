package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

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

    @Positive(message = "Укажите положительное число!")
    private Integer duration;
    private Mpa mpa;
    private List<Genre> genres;

    public boolean hasGenres() {
        return !CollectionUtils.isEmpty(genres);
    }

    public boolean hasMpa() {
        return mpa != null;
    }
}
