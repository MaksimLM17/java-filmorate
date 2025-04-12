package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    public Collection<Film> getAll();

    public Film create(Film film);

    public Film update(Film film);

    public void addLike(Integer id, Integer userId);

    public void removeLike(Integer id, Integer userId);

    public Collection<Film> getPopularFilms(Integer count);

    public boolean checkIdStorage(Integer id);
}
