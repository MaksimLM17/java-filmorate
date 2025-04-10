package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<FilmDto> getAll() {
        log.debug("Получен запрос на получение списка фильмов.");
        return filmStorage.getAll();
    }

    public FilmDto create(FilmDto filmDto) {
        log.debug("Получен запрос на добавление фильма с данными: {}", filmDto);
        return filmStorage.create(filmDto);
    }

    public FilmDto update(FilmDto filmDto) {
        log.debug("Получен запрос на обновление фильма с данными: {}", filmDto);
        return filmStorage.update(filmDto);
    }

    public void addLike(Integer id, Integer userId) {
        log.debug("Получен запрос на добавление лайка с данными id {}, userId {}", id, userId);
        checkId(id, userId);
        filmStorage.addLike(id, userId);
    }

    public void removeLike(Integer id, Integer userId) {
        log.debug("Получен запрос на удаление лайка с данными id {}, userId {}", id, userId);
        checkId(id, userId);
        filmStorage.removeLike(id, userId);
    }

    public Collection<FilmDto> getPopularFilms(Integer count) {
        log.debug("Получен запрос на получение списка самых популярных фильмов с данными count: {}", count);
        if (count < 1) {
            log.error("Некорректное значение count {}", count);
            throw new BadRequestException("Ограничение по количеству для вывода самых популярных фильмов" +
                    " должно быть больше нуля!");
        }
        return filmStorage.getPopularFilms(count);
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
}
