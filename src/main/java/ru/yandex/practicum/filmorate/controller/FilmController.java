package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

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
    public Collection<Film> getFilms() {
        if (films.isEmpty()) {
            log.warn("Ошибка валидации, список фильмов пуст!");
            throw new NotFoundException("Список фильмов пуст!");
        }
        log.info("Получен список фильмов.");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        LocalDate dateStart = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(dateStart)) {
            log.error("Дата релиза указана некорректно: {}", film.getReleaseDate());
            throw new BadRequestException("Дата релиза указана раньше, чем был снят первый фильм!");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм: {} ,добавлен", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        Integer id = film.getId();
        if (id == null) {
            log.error("Не указан id при запросе на обновление фильма");
            throw new BadRequestException("Id должен быть указан");
        }
        if (!films.containsKey(id)) {
            log.error("По переданному id {} фильм не найден.", id);
            throw new NotFoundException(String.format("По переданному id: %d, фильм не обнаружен",id));
        }
        Film newFilm = films.get(id);
        if (film.getName() != null) {
            newFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            newFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            newFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() != null) {
            newFilm.setDuration(film.getDuration());
        }

        films.put(id,newFilm);
        log.info("Фильм с id {}, обновлен", id);
        return newFilm;
    }

    private int getNextId() {
        int currentMaxId = films.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
