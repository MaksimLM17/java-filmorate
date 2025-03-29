package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<FilmDto> getFilms() {
        if (films.isEmpty()) {
            log.warn("Ошибка валидации, список фильмов пуст!");
            throw new NotFoundException("Список фильмов пуст!");
        }
        log.info("Получен список фильмов.");
        return films.values().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public FilmDto createFilmDto(@RequestBody @Valid FilmDto filmDto) {
        LocalDate dateStart = LocalDate.of(1895, 12, 28);
        if (filmDto.getReleaseDate().isBefore(dateStart)) {
            log.error("Дата релиза указана некорректно: {}", filmDto.getReleaseDate());
            throw new BadRequestException("Дата релиза указана раньше, чем был снят первый фильм!");
        }
        Film film = toEntity(filmDto);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм: {} , добавлен", film.getName());
        return toDto(film);
    }

    @PutMapping
    public FilmDto updateFilmDto(@RequestBody @Valid FilmDto filmDto) {
        Integer id = filmDto.getId();
        if (id == null) {
            log.error("Не указан id при запросе на обновление фильма");
            throw new BadRequestException("Id должен быть указан");
        }
        if (!films.containsKey(id)) {
            log.error("По переданному id {} фильм не найден.", id);
            throw new NotFoundException(String.format("По переданному id: %d, фильм не обнаружен", id));
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
            newFilm.setDuration(filmDto.getDuration());
        }

        films.put(id, newFilm);
        log.info("Фильм с id {}, обновлен", id);
        return toDto(newFilm);
    }


    private int getNextId() {
        int currentMaxId = films.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private FilmDto toDto(Film film) {
        return new FilmDto(film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration());
    }

    private Film toEntity(FilmDto filmDto) {
        Film film = new Film();
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        return film;
    }
}
