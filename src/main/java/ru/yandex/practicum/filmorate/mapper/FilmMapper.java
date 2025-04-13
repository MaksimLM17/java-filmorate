package ru.yandex.practicum.filmorate.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
@Slf4j
public class FilmMapper {

    public FilmDto convertToDto(Film film) {
        return new FilmDto(film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration());
    }

    public Film convertToEntity(FilmDto filmDto) {
        Film film = new Film();
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        return film;
    }

    public void validateDateRelease(LocalDate date) {
        LocalDate dateStart = LocalDate.of(1895, 12, 28);
        if (date.isBefore(dateStart)) {
            log.error("Дата релиза указана некорректно: {}", date);
            throw new BadRequestException("Дата релиза указана раньше, чем был снят первый фильм!");
        }
    }
}
