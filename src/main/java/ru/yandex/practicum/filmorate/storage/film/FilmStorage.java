package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.util.Collection;

public interface FilmStorage {

    public Collection<FilmDto> getAll();

    public FilmDto create(FilmDto filmDto);

    public FilmDto update(FilmDto filmDto);

    public void addLike(Integer id, Integer userId);

    public void removeLike(Integer id, Integer userId);

    public Collection<FilmDto> getPopularFilms(Integer count);
}
