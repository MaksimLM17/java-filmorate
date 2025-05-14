package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    public Collection<Mpa> getAll() {
        log.debug("Получен запрос на получение списка рейтингов фильмов");
        if (mpaStorage.getAll().isEmpty()) {
            log.warn("Ошибка валидации, список с рейтингами пуст");
            throw new NotFoundException("Список с рейтингами пуст");
        }
        log.info("Отправлен список с рейтингами");
        return mpaStorage.getAll().stream()
                .sorted(Comparator.comparing(Mpa::getId))
                .toList();
    }

    public Mpa getById(Integer id) {
        return mpaStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден по id: " + id));
    }

    private void checkId(Integer id) {
        if (id < 1) {
            log.error("Передано неверное значение id = {}", id);
            throw new BadRequestException("Id не может быть меньше либо равно нулю");
        }
    }
}
