package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Dto.FilmDto;
import ru.yandex.practicum.filmorate.Exception.ValidationExceptionBadRequest;
import ru.yandex.practicum.filmorate.Exception.ValidationExceptionNotFound;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<FilmDto> getFilms() {
        if (films.isEmpty()) {
            log.warn("Ошибка валидации, список фильмов пуст!");
            throw new ValidationExceptionNotFound("Список фильмов пуст!");
        }
        log.info("Получен список фильмов.");
        return films.values().stream()
                .map(this::convertFilm)
                .toList();
    }

    @PostMapping
    public FilmDto createFilm(@RequestBody @Valid Film film) {
        LocalDate dateStart = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(dateStart)) {
            log.error("Дата релиза указана некорректно: {}", film.getReleaseDate());
            throw new ValidationExceptionBadRequest("Дата релиза указана раньше, чем был снят первый фильм!");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм: {} ,добавлен", film.getName());
        return convertFilm(film);
    }

    @PutMapping
    public FilmDto updateFilm(@RequestBody @Valid FilmDto filmDto) {
        Integer id = filmDto.getId();
        if (id == null) {
            log.error("Не указан id при запросе на обновление фильма");
            throw new ValidationExceptionNotFound("Id должен быть указан");
        }
        if (!films.containsKey(id)) {
            log.error("По переданному id {} фильм не найден.", id);
            throw new ValidationExceptionNotFound(String.format("По переданному id: %d, фильм не обнаружен",id));
        }
        Film newFilm = films.get(id);
        if (filmDto.getName() != null) {
            newFilm.setName(filmDto.getName());
        }
        if (filmDto.getDescription() != null) {
            newFilm.setDescription(filmDto.getDescription());
        }
        if (filmDto.getReleaseDate() != null) {
            newFilm.setReleaseDate(filmDto.getReleaseDate());
        }
        if (filmDto.getDuration() != null) {
            Duration duration = Duration.ofSeconds(filmDto.getDuration());
            newFilm.setDuration(duration);
        }

        films.put(id,newFilm);
        log.info("Фильм с id {}, обновлен", id);
        return convertFilm(newFilm);
    }

    private int getNextId() {
        int currentMaxId = films.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private FilmDto convertFilm(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration().toSeconds());
        return dto;
    }
}
