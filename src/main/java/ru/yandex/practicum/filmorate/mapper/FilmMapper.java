package ru.yandex.practicum.filmorate.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class FilmMapper {

    public FilmDto convertToDto(Film film) {
        return new FilmDto(film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa(), film.getGenres());
    }

    public Film convertToEntity(FilmDto filmDto) {
        Film film = new Film();
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        if (filmDto.hasMpa()) {
            film.setMpa(filmDto.getMpa());
        }
        if (filmDto.hasGenres()) {
            film.setGenres(filmDto.getGenres());
        }
        return film;
    }


    public Film updateFilm(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasMpa()) {
            film.setMpa(request.getMpa());
        }
        if (request.hasGenres()) {
            film.setGenres(request.getGenres());
        }
        return film;
    }

    public void validateFields(FilmDto filmDto) {
        validateDateRelease(filmDto.getReleaseDate());
        validateMpa(filmDto);
        validateGenre(filmDto);
        checkDuplicateGenre(filmDto);
    }

    private void validateDateRelease(LocalDate date) {
        LocalDate dateStart = LocalDate.of(1895, 12, 28);
        if (date.isBefore(dateStart)) {
            log.error("Дата релиза указана некорректно: {}", date);
            throw new BadRequestException("Дата релиза указана раньше, чем был снят первый фильм!");
        }
    }

    private void validateGenre(FilmDto filmDto) {
        if (filmDto.hasGenres()) {
            List<Integer> checkGenre = filmDto.getGenres().stream()
                    .map(Genre::getId)
                    .toList();
            for (Integer value : checkGenre) {
                if (value <= 0 || value > 6) {
                    log.error("Передан некорректный id жанра: {}", value);
                    throw new NotFoundException("Жанр не найден по переданному id = " + value);
                }
            }
        }
    }

    private void validateMpa(FilmDto filmDto) {
        if (filmDto.hasMpa()) {
            Integer id = filmDto.getMpa().getId();
            if (id <= 0 || id > 5) {
                log.error("Передан некорректный id рейтинга: {}", id);
                throw new NotFoundException("Рейтинг не найден по переданному id = " + id);
            }
        }
    }

    private void checkDuplicateGenre(FilmDto filmDto) {
        if (filmDto.hasGenres()) {
            Set<Genre> uniqueGenres = new HashSet<>(filmDto.getGenres());
            if (uniqueGenres.size() != filmDto.getGenres().size()) {
                log.info("При передаче запроса обнаружены дубликаты жанров!");
                filmDto.setGenres(uniqueGenres.stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .toList());
            }
        }
    }
}
