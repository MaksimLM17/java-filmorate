package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenreStorage genreStorage;

    public Collection<Genre> getAll() {
        log.debug("Получен запрос на получение списка жанров");
        if (genreStorage.getAll().isEmpty()) {
            log.warn("Ошибка валидации, список жанров пуст");
            throw new NotFoundException("Список жанров пуст");
        }
        log.info("Отправлен список жанров");
        return genreStorage.getAll().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .toList();
    }

    public Genre getById(Integer id) {
        checkId(id);
        return genreStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с данным id не найден: " + id));
    }

    private void checkId(Integer id) {
        if (id < 1) {
            log.error("Передано неверное значение id = {}", id);
            throw new BadRequestException("Id не может быть меньше либо равно нулю");
        }
    }
}
