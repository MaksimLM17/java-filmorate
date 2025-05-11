package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Qualifier("filmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

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
    public void addLike(Integer filmId, Integer userId) {
        likes.putIfAbsent(filmId, new HashSet<>());
        likes.get(filmId).add(userId);
        log.info("Фильму с id: {} поставлен лайк, пользователем: {}", filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        if (likes.containsKey(filmId)) {
            Set<Integer> userLikes = likes.get(filmId);
            if (userLikes.contains(userId)) {
                userLikes.remove(userId);
                log.info("У фильма с id: {} удален лайк, пользователем: {}", filmId, userId);
                if (userLikes.isEmpty()) {
                    likes.remove(filmId);
                } else {
                    likes.remove(filmId);
                    likes.put(filmId,userLikes);
                }
            }
        }
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        List<Film> popularFilms = new ArrayList<>(films.values());

        popularFilms.sort((film1, film2) -> {
            int likes1 = likes.getOrDefault(film1.getId(), Collections.emptySet()).size();
            int likes2 = likes.getOrDefault(film2.getId(), Collections.emptySet()).size();
            return Integer.compare(likes2, likes1);
        });

        return popularFilms.stream().limit(count).toList();
    }

    @Override
    public boolean checkIdStorage(Integer id) {
        return films.containsKey(id);
    }

    @Override
    public Film getById(Integer id) {
        return films.get(id);
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
        if (newFilm.getGenres() != null) {
            film.setGenres(newFilm.getGenres());
        }
        if (newFilm.getMpa() != null) {
            film.setMpa(newFilm.getMpa());
        }
        return film;
    }
}
