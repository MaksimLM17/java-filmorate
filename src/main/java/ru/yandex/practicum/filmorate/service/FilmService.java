package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmMapper filmMapper;

    public Collection<FilmDto> getAll() {
        log.debug("Получен запрос на получение списка фильмов.");
        if (filmStorage.getAll().isEmpty()) {
            log.warn("Ошибка валидации, список фильмов пуст!");
            throw new NotFoundException("Список фильмов пуст!");
        }
        log.info("Отправлен список фильмов.");
        return filmStorage.getAll().stream()
                .map(filmMapper::convertToDto)
                .toList();
    }

    public FilmDto create(FilmDto filmDto) {
        log.debug("Получен запрос на добавление фильма с данными: {}", filmDto);
        filmMapper.validateDateRelease(filmDto.getReleaseDate());
        Film film = filmMapper.convertToEntity(filmDto);
        return filmMapper.convertToDto(filmStorage.create(film));
    }

    public FilmDto update(FilmDto filmDto) {
        log.debug("Получен запрос на обновление фильма с данными: {}", filmDto);
        Integer id = filmDto.getId();
        validateFilmId(id);
        Film film = filmMapper.convertToEntity(filmDto);
        return filmMapper.convertToDto(filmStorage.update(film));
    }

    public void addLike(Integer id, Integer userId) {
        log.debug("Получен запрос на добавление лайка с данными id {}, userId {}", id, userId);
        checkId(id, userId);
        validateFilmId(id);
        filmStorage.addLike(id, userId);
    }

    public void removeLike(Integer id, Integer userId) {
        log.debug("Получен запрос на удаление лайка с данными id {}, userId {}", id, userId);
        checkId(id, userId);
        validateFilmId(id);
        filmStorage.removeLike(id, userId);
    }

    public Collection<FilmDto> getPopularFilms(Integer count) {
        log.debug("Получен запрос на получение списка самых популярных фильмов с данными count: {}", count);
        if (count < 1) {
            log.error("Некорректное значение count {}", count);
            throw new BadRequestException("Ограничение по количеству для вывода самых популярных фильмов" +
                    " должно быть больше нуля!");
        }
        return filmStorage.getPopularFilms(count).stream()
                .map(filmMapper::convertToDto)
                .toList();
    }

    private void checkId(Integer id, Integer userId) {
        if (id <= 0 || userId <= 0) {
            log.error("В запросе указан некорректный id: {}", id);
            throw new BadRequestException("Id не может быть меньше либо равно нулю");
        }
        if (!userStorage.checkUserId(userId)) {
            log.error("В запросе указан некорректный userId: {}", userId);
            throw new NotFoundException(String.format("Пользователь не найден по данному %d", userId));
        }
    }

    private void validateFilmId(Integer id) {
        if (id == null) {
            log.error("Не указан id при запросе на обновление фильма");
            throw new BadRequestException("Id должен быть указан");
        }
        if (id <= 0) {
            log.error("В запросе указан некорректный id: {}", id);
            throw new BadRequestException("Id не может быть меньше либо равно нулю");
        }
        if (!filmStorage.checkIdStorage(id)) {
            log.error("По переданному id {} фильм не найден.", id);
            throw new NotFoundException(String.format("По переданному id: %d, фильм не обнаружен", id));
        }
    }
}
