package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<FilmDto> getAll() {
        if (films.isEmpty()) {
            log.warn("Ошибка валидации, список фильмов пуст!");
            throw new NotFoundException("Список фильмов пуст!");
        }
        log.info("Отправлен список фильмов.");
        return films.values().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FilmDto create(FilmDto filmDto) {
        validateDateRelease(filmDto.getReleaseDate());
        Film film = convertToEntity(filmDto);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм: {} , добавлен", film.getName());
        return convertToDto(film);
    }

    @Override
    public FilmDto update(FilmDto filmDto) {
        Integer id = filmDto.getId();
        validateFilmId(id);

        Film newFilm = updateData(films.get(id), filmDto);
        films.put(id, newFilm);
        log.info("Фильм с id {}, обновлен", id);
        return convertToDto(newFilm);
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        checkId(id);
        Film film = films.get(id);
        film.setLike(userId);
        log.info("Фильму с id: {} поставлен лайк, пользователем: {}", id, userId);
    }

    @Override
    public void removeLike(Integer id, Integer userId) {
        checkId(id);
        Film film = films.get(id);
        film.removeLike(userId);
        log.info("У фильма с id: {} удален лайк, пользователем: {}", id, userId);
    }

    @Override
    public Collection<FilmDto> getPopularFilms(Integer count) {
        List<Film> sortedLikesFilm = films.values().stream()
                .sorted(Comparator.comparing(Film::countLikes).reversed())
                .limit(count)
                .toList();
        return sortedLikesFilm.stream()
                .map(this::convertToDto)
                .toList();
    }

    private int getNextId() {
        int currentMaxId = films.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private FilmDto convertToDto(Film film) {
        return new FilmDto(film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration());
    }

    private void validateFilmId(Integer id) {
        if (id == null) {
            log.error("Не указан id при запросе на обновление фильма");
            throw new BadRequestException("Id должен быть указан");
        }
        if (!films.containsKey(id)) {
            log.error("По переданному id {} фильм не найден.", id);
            throw new NotFoundException(String.format("По переданному id: %d, фильм не обнаружен", id));
        }
    }

    private void validateDateRelease(LocalDate date) {
        LocalDate dateStart = LocalDate.of(1895, 12, 28);
        if (date.isBefore(dateStart)) {
            log.error("Дата релиза указана некорректно: {}", date);
            throw new BadRequestException("Дата релиза указана раньше, чем был снят первый фильм!");
        }
    }

    private void checkId(Integer id) {
        if (!films.containsKey(id)) {
            log.error("По данному id: {}, фильм не найден!",id);
            throw new NotFoundException(String.format("По данному id: %d, фильм не найден!",id));
        }
    }

    private Film convertToEntity(FilmDto filmDto) {
        Film film = new Film();
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        return film;
    }

    private Film updateData(Film film, FilmDto filmDto) {
        if (filmDto.getName() != null) {
            film.setName(filmDto.getName());
        }
        if (filmDto.getDescription() != null) {
            film.setDescription(filmDto.getDescription());
        }
        if (filmDto.getReleaseDate() != null) {
            film.setReleaseDate(filmDto.getReleaseDate());
        }
        if (filmDto.getDuration() != null) {
            film.setDuration(filmDto.getDuration());
        }
        return film;
    }
}
