package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм: {} , добавлен", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        Integer id = film.getId();
        Film newFilm = updateData(films.get(id), film);
        films.put(id, newFilm);
        log.info("Фильм с id {}, обновлен", id);
        return newFilm;
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        Film film = films.get(id);
        film.setLike(userId);
        log.info("Фильму с id: {} поставлен лайк, пользователем: {}", id, userId);
    }

    @Override
    public void removeLike(Integer id, Integer userId) {
        Film film = films.get(id);
        film.removeLike(userId);
        log.info("У фильма с id: {} удален лайк, пользователем: {}", id, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::countLikes).reversed())
                .limit(count)
                .toList();
    }

    @Override
    public boolean checkIdStorage(Integer id) {
        return films.containsKey(id);
    }

    private int getNextId() {
        int currentMaxId = films.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private Film updateData(Film film, Film newFilm) {
        if (newFilm.getName() != null) {
            film.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            film.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            film.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            film.setDuration(newFilm.getDuration());
        }
        return film;
    }
}
